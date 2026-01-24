package work.soho.common.core.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageSerializable;
import lombok.experimental.UtilityClass;
import work.soho.common.core.result.R;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@UtilityClass
public class PageUtils {
    /**
     * 启动分页
     */
    public void startPage() {
        int pageSize = 20;
        int pageNum = 1;
        String pageNumStr = RequestUtil.getRequest().getParameter("page");
        String pageSizeStr = RequestUtil.getRequest().getParameter("pageSize");
        if(StringUtils.isNotEmpty(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        if(StringUtils.isNotEmpty(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }

        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * ⭐ PageHelper 通用分页执行（核心）
     *
     * @param querySupplier 只写一次 list 查询
     * @param mapper        entity -> VO
     */
    public <T, R> PageSerializable<R> page(
            Supplier<List<T>> querySupplier,
            Function<T, R> mapper
    ) {
        Objects.requireNonNull(querySupplier, "querySupplier cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        // 统一在这里 startPage
        startPage();

        List<T> list = querySupplier.get();
        if (list == null) {
            list = Collections.emptyList();
        }

        PageInfo<T> pageInfo = new PageInfo<>(list);

        List<R> voList = list.stream()
                .map(mapper)
                .collect(Collectors.toList());

        PageSerializable<R> pageSerializable = new PageSerializable<>(voList);
        pageSerializable.setTotal(pageInfo.getTotal());

        return pageSerializable;
    }

    /**
     * ⭐ 直接返回 R.success(...) 的快捷方法
     */
    public <T, X> R<PageSerializable<X>> pageR(
            Supplier<List<T>> querySupplier,
            Function<T, X> mapper
    ) {
        return R.success(page(querySupplier, mapper));
    }

    /**
     * ⭐复杂聚合分页：主表分页查询 + VO 映射 + 批量回填关联数据（B/C/D...）
     *
     * @param baseQuerySupplier 主表分页查询（只在这里执行一次 SQL，PageHelper 生效）
     * @param mapper           主表 entity -> VO
     * @param filler           批量填充器：给你本页 voList，你在里面批量查关联表并回填（避免 N+1）
     */
    public <T, V> PageSerializable<V> pageFill(
            Supplier<List<T>> baseQuerySupplier,
            Function<T, V> mapper,
            Consumer<List<V>> filler
    ) {
        startPage();
        List<T> baseList = Optional.ofNullable(baseQuerySupplier.get()).orElse(Collections.emptyList());
        PageInfo<T> pageInfo = new PageInfo<>(baseList);

        List<V> voList = baseList.stream().map(mapper).collect(Collectors.toList());

        if (filler != null && !voList.isEmpty()) {
            filler.accept(voList); // 在这里批量查询 B/C/D... 并回填到 voList
        }

        PageSerializable<V> pageSerializable = new PageSerializable<>(voList);
        pageSerializable.setTotal(pageInfo.getTotal());
        return pageSerializable;
    }

    /**
     * ⭐复杂聚合分页：主表分页查询 + VO 映射 + 批量回填关联数据（B/C/D...）
     *
     * example:
     * <pre>
     *          LambdaQueryWrapper<UserOauthType> lqw = buildWrapper(...);
     *
     *     return PageUtils.pageFillR(
     *         () -> userOauthTypeService.list(lqw),                    // ① 主表分页查询（PageHelper 生效）
     *         e -> BeanUtils.copy(e, UserOauthTypeVo.class),           // ② 主表映射 VO
     *         voList -> {                                              // ③ 批量回填（只做 IN 查询，别循环查）
     *
     *             // 例：一对一（provider）
     *             Map<Long, OauthProvider> providerMap = PageUtils.loadMap(
     *                 voList,
     *                 UserOauthTypeVo::getProviderId,                  // 从 VO 提 key（来自主表字段）
     *                 ids -> oauthProviderService.listByIds(ids),      // 批量查 B 表
     *                 OauthProvider::getId
     *             );
     *             voList.forEach(vo -> vo.setProvider(providerMap.get(vo.getProviderId())));
     *
     *             // 例：一对多（scopes）
     *             Map<Long, List<OauthScope>> scopeGroup = PageUtils.loadGroup(
     *                 voList,
     *                 UserOauthTypeVo::getId,                          // 主键 typeId
     *                 ids -> oauthScopeService.list(
     *                         new LambdaQueryWrapper<OauthScope>().in(OauthScope::getTypeId, ids)
     *                 ),
     *                 OauthScope::getTypeId
     *             );
     *             voList.forEach(vo -> vo.setScopes(scopeGroup.getOrDefault(vo.getId(), List.of())));
     *
     *             // 例：再查一张表（创建人信息）
     *             Map<String, AdminUser> userMap = PageUtils.loadMap(
     *                 voList,
     *                 UserOauthTypeVo::getCreatedBy,                   // createdBy
     *                 uids -> adminUserService.list(
     *                         new LambdaQueryWrapper<AdminUser>().in(AdminUser::getUserId, uids)
     *                 ),
     *                 AdminUser::getUserId
     *             );
     *             voList.forEach(vo -> vo.setCreatedByUser(userMap.get(vo.getCreatedBy())));
     *         }
     *     );
     * </pre>
     *
     * @param baseQuerySupplier 主表分页查询（只在这里执行一次 SQL，PageHelper 生效）
     * @param mapper           主表 entity -> VO
     * @param filler           批量填充器：给你本页 voList，你在里面批量查关联表并回填（避免 N+1）
     */
    public <T, V> R<PageSerializable<V>> pageFillR(
            Supplier<List<T>> baseQuerySupplier,
            Function<T, V> mapper,
            Consumer<List<V>> filler
    ) {
        return R.success(pageFill(baseQuerySupplier, mapper, filler));
    }

    // -------- 可选：批量加载小工具，让 filler 写得更短 --------

    /**
     * 从 voList 提取 keys（去重）→ 批量加载 → 生成 Map
     */
    public <V, K, D> Map<K, D> loadMap(
            List<V> voList,
            Function<V, K> keyExtractor,
            Function<Collection<K>, List<D>> batchLoader,
            Function<D, K> dataKeyExtractor
    ) {
        if (voList == null || voList.isEmpty()) return Collections.emptyMap();

        List<K> keys = voList.stream()
                .map(keyExtractor)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (keys.isEmpty()) return Collections.emptyMap();

        List<D> dataList = Optional.ofNullable(batchLoader.apply(keys)).orElse(Collections.emptyList());
        return dataList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(dataKeyExtractor, Function.identity(), (a, b) -> a));
    }

    /**
     * 一对多：key -> List<D>
     */
    public <V, K, D> Map<K, List<D>> loadGroup(
            List<V> voList,
            Function<V, K> keyExtractor,
            Function<Collection<K>, List<D>> batchLoader,
            Function<D, K> dataKeyExtractor
    ) {
        if (voList == null || voList.isEmpty()) return Collections.emptyMap();

        List<K> keys = voList.stream()
                .map(keyExtractor)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (keys.isEmpty()) return Collections.emptyMap();

        List<D> dataList = Optional.ofNullable(batchLoader.apply(keys)).orElse(Collections.emptyList());
        return dataList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(dataKeyExtractor));
    }
}
