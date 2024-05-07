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
import work.soho.common.core.util.TreeUtils;
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
    @Node(value = "resource::admin-resource-sync", name = "同步项目资源")
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
    @Node(value = "resource::routeVoList", name = "路由资源列表")
    @GetMapping("/routes")
    public List<RouteVo> routeVoList() {
        Long userId = SecurityUtils.getLoginUserId();
        List<AdminResource> list = adminResourceService.list(
                Wrappers.<AdminResource>lambdaQuery()
                        .eq(AdminResource::getType, 1) // 1 前端节点
        );
        //格式转换
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
            List<Long> roleResourceRouteIds = roleResourceList.stream().map(AdminRoleResource::getResourceId).collect(Collectors.toList());

            try {
                Class<?> c = RouteVo.class;
                TreeUtils<Long, RouteVo> treeUtils = new TreeUtils();
                treeUtils.loadData(routeDTOList, c.getMethod("getId"), c.getMethod("getBreadcrumbParentId"));
                List<RouteVo> needList = treeUtils.getAllTreeNodeWidthIds(roleResourceRouteIds);
                needList.addAll(treeUtils.getAllParentByIds(roleResourceRouteIds));
                routeDTOList = needList;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // 排序
        return routeDTOList.stream().sorted(Comparator.comparingInt(RouteVo::getSort)).collect(Collectors.toList());
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
    @Node(value = "resource::details", name = "详情")
    public R<AdminResource> getDetails(Long id) {
        AdminResource adminResource = adminResourceService.getById(id);
        return R.success(adminResource);
    }

    @ApiOperation("更新资源")
    @PutMapping
    @Node(value = "resource::update", name = "更新")
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
    @Node(value = "resource::create", name = "新增")
    public R<AdminResource> create(@RequestBody AdminResource adminResource) {
        adminResource.setCreatedTime(new Date());
        adminResourceService.save(adminResource);
        return R.success(adminResource);
    }

    @ApiOperation("删除指定资源")
    @DeleteMapping()
    @Node(value = "resource::delete", name = "删除")
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
