package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.utils.SecurityUtils;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.domain.AdminRoleResource;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.service.AdminResourceService;
import work.soho.admin.service.AdminRoleResourceService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.vo.RouteVo;
import work.soho.api.admin.vo.TreeResourceVo;
import work.soho.common.core.result.R;

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "菜单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/adminResource")
public class AdminResourceController {
    private final AdminResourceService adminResourceService;
    private final AdminRoleUserService adminRoleUserService;
    private final AdminRoleResourceService adminRoleResourceService;

    @ApiOperation("同步菜单")
    @Node(value = "admin-resource-sync", name = "同比项目资源")
    @GetMapping("/admin-resource/sync")
    public R<Boolean> syncResource() {
        adminResourceService.syncResource2Db();
        return R.success();
    }

    /**
     * 前端页面获取导航菜单信息
     *
     * @return
     */
    @GetMapping("/routes")
    public List<RouteVo> routeVoList() {
        Long userId = SecurityUtils.getLoginUserId();
        List<AdminResource> list = adminResourceService.list(
                Wrappers.<AdminResource>lambdaQuery()
                        .eq(AdminResource::getType, 1) // 1 前端节点
                        .eq(AdminResource::getVisible, 1) // 1 可见菜单
        );

        List<RouteVo> routeDTOList = list.stream().map(item -> {
            RouteVo route = new RouteVo();
            BeanUtils.copyProperties(item, route);
            route.setIcon(item.getIconName());
            route.setMenuParentId(route.getBreadcrumbParentId());
            RouteVo.Langues zh = new RouteVo.Langues();
            zh.setName(item.getZhName());
            route.setZh(zh);
            route.setVisible(item.getVisible() == 1);
            return route;
        }).collect(Collectors.toList());

        // 按 parentId 分组
        Map<Long, List<RouteVo>> resourceMap = routeDTOList.stream().collect(Collectors.groupingBy(RouteVo::getBreadcrumbParentId));

        // 非 admin 用户
        if(!Objects.equals(1L, userId)){
            // 查询用户角色
            List<AdminRoleUser> roleUsers = adminRoleUserService.list(
                    Wrappers.<AdminRoleUser>lambdaQuery()
                            .eq(AdminRoleUser::getUserId, userId)
                            .eq(AdminRoleUser::getStatus, 1) //1 启用的角色 0 放弃的角色
            );
            // 拥有的角色IDS
            List<Long> roleIds = roleUsers.stream().map(AdminRoleUser::getRoleId).collect(Collectors.toList());

            // 查询用户角色绑定的菜单
            List<AdminRoleResource> roleResourceList = adminRoleResourceService.list(
                    Wrappers.<AdminRoleResource>lambdaQuery()
                            .in(AdminRoleResource::getRoleId, roleIds)
            );

            // 角色绑定的菜单IDS
            Set<Long> roleResourceRouteIds = roleResourceList.stream().map(AdminRoleResource::getResourceId).collect(Collectors.toSet());

            // 有权限的菜单
            List<RouteVo> hasRoleResource = routeDTOList.stream().filter(e -> roleResourceRouteIds.contains(e.getId())).collect(Collectors.toList());
            Set<Long> collect = hasRoleResource.stream().map(RouteVo::getBreadcrumbParentId).collect(Collectors.toSet());
            roleResourceRouteIds.addAll(collect);

            // 角色菜单全选子节点不会存入数据库，需要把子节点找出来
            Set<Long> parentIds = roleResourceRouteIds.stream().filter(e -> Objects.isNull(resourceMap.get(e))).collect(Collectors.toSet());
            Set<Long> childrenSet = routeDTOList.stream().filter(route -> parentIds.contains(route.getBreadcrumbParentId())).map(RouteVo::getId).collect(Collectors.toSet());
            roleResourceRouteIds.addAll(childrenSet);

            // 菜单筛选
            routeDTOList = routeDTOList.stream().filter(route -> roleResourceRouteIds.contains(route.getId())).collect(Collectors.toList());
        }

        // 设置子节点
        routeDTOList.forEach(route -> route.setChildren(resourceMap.get(route.getId())));

        // 筛选树节点
        List<RouteVo> treeRoute = routeDTOList.stream().filter(route -> Objects.equals(1L, route.getBreadcrumbParentId())).collect(Collectors.toList());

        List<RouteVo> data = new ArrayList<>();
        // 树节点转list
        this.treeToList(treeRoute, data);
        // 排序
        data = data.stream().sorted(Comparator.comparingInt(RouteVo::getSort)).collect(Collectors.toList());

        // 返回菜单数据
        return data;
    }

    private void treeToList (List<RouteVo> tree, List<RouteVo> result){
        for (RouteVo routeDTO : tree) {
            result.add(routeDTO);

            if(routeDTO.getChildren()!=null && !routeDTO.getChildren().isEmpty()){
                this.treeToList(routeDTO.getChildren(), result);
            }
        }
    }

    private void loopFillResourceId(Map<Long, List<AdminResource>> parentMap, Long resourceId, HashMap<Long, AdminResource> map) {
        if (parentMap.get(resourceId) == null) {
            return;
        }
        for (AdminResource item : parentMap.get(resourceId)) {
            map.put(item.getId(), item);
            loopFillResourceId(parentMap, item.getId(), map);
        }
    }

    @ApiOperation("资源详情")
    @GetMapping()
    public R<AdminResource> getDetails(Long id) {
        AdminResource adminResource = adminResourceService.getById(id);
        return R.success(adminResource);
    }

    @ApiOperation("更新资源")
    @PutMapping
    public R<Boolean> update(@RequestBody AdminResource adminResource) {
        //TODO 资源检查
        adminResourceService.updateById(adminResource);
        return R.success(true);
    }

    /**
     * 创建资源
     *
     * @param adminResource
     * @return
     */
    @PostMapping
    @ApiOperation("创建资源")
    public R<AdminResource> create(@RequestBody AdminResource adminResource) {
        adminResource.setCreatedTime(new Date());
        adminResourceService.save(adminResource);
        return R.success(adminResource);
    }

    @ApiOperation("删除指定资源")
    @DeleteMapping()
    public R<Boolean> delete(@RequestBody AdminResource adminResource) {
        AdminResource dbAdminResource = adminResourceService.getById(adminResource.getId());
        //检查是否有子节点
        LambdaQueryWrapper<AdminResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AdminResource::getBreadcrumbParentId, dbAdminResource.getId());
        List<AdminResource> list = adminResourceService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        adminResourceService.removeById(dbAdminResource.getId());
        return R.success(true);
    }

    /**
     * 获取资源树
     * <p>
     * TODO 根据登录用户进行过滤
     */
    @GetMapping("/tree")
    public R<TreeResourceVo> getResourceTree(String language) {
        LambdaQueryWrapper<AdminResource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(AdminResource::getSort);
        List<AdminResource> list = adminResourceService.list(lambdaQueryWrapper);
        Map<Long, List<AdminResource>> parentList = new HashMap<>();
        //检查language, 默认为中文
        if (language == null || "".equals(language)) {
            language = "zh";
        }
        //构造parent -> son list
        for (AdminResource item : list) {
            Long parentId = item.getBreadcrumbParentId();
            parentList.computeIfAbsent(parentId, k -> new ArrayList<AdminResource>());
            List<AdminResource> tmpParentList = parentList.get(parentId);
            tmpParentList.add(item);
            parentList.put(parentId, tmpParentList);
        }

        //构造treevo
        return R.success(getTree(0l, parentList, language).get(0));
    }

    /**
     * 获取资源树
     *
     * @param parentId
     * @param parentMap
     * @return
     */
    private List<TreeResourceVo> getTree(Long parentId, Map<Long, List<AdminResource>> parentMap, String language) {
        List<TreeResourceVo> tree = new ArrayList<>();
        if (parentMap.get(parentId) != null) {
            for (AdminResource resource : parentMap.get(parentId)) {
                TreeResourceVo resourceVo = new TreeResourceVo();
                resourceVo.setKey(String.valueOf(resource.getId()));
                if (language.equals("en")) {
                    resourceVo.setTitle(resource.getName());
                } else {
                    resourceVo.setTitle(resource.getZhName());
                }

                resourceVo.setValue(String.valueOf(resource.getId()));
                //递归查询子资源
                resourceVo.setChildren(getTree(resource.getId(), parentMap, language));
                tree.add(resourceVo);
            }
        }
        return tree;
    }
}
