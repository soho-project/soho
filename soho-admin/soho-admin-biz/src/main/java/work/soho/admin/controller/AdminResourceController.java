package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.annotation.Node;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminResourceService;
import work.soho.api.admin.vo.RouteVo;
import work.soho.api.admin.vo.TreeResourceVo;
import work.soho.common.core.result.R;

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "菜单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin-resource")
public class AdminResourceController {
    private final AdminResourceService adminResourceService;

    @ApiOperation("同步菜单")
    @Node(value = "admin-resource-sync", name = "同比项目资源", describe = "")
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
        LambdaQueryWrapper<AdminResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AdminResource::getType, 1); //前端节点
        lqw.eq(AdminResource::getVisible, 1); //可见菜单
        lqw.orderByAsc(AdminResource::getSort);
        List<AdminResource> list = adminResourceService.list(lqw);
        return list.stream().map(item->{
            RouteVo routeVo = new RouteVo();
            BeanUtils.copyProperties(item, routeVo);
            routeVo.setMenuParentId(routeVo.getBreadcrumbParentId());
            RouteVo.Langues zh = new RouteVo.Langues();
            zh.setName(item.getZhName());
            routeVo.setZh(zh);
            return routeVo;
        }).collect(Collectors.toList());
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
        if(!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        adminResourceService.removeById(dbAdminResource.getId());
        return R.success(true);
    }

    /**
     * 获取资源树
     *
     * TODO 根据登录用户进行过滤
     */
    @GetMapping("/tree")
    public R<TreeResourceVo> getResourceTree(String language) {
        List<AdminResource> list = adminResourceService.list();
        Map<Long, List<AdminResource>> parentList = new HashMap<>();
        //检查language, 默认为中文
        if(language == null || "".equals(language)) {
            language = "zh";
        }
        //构造parent -> son list
        for (AdminResource item: list) {
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
    private List<TreeResourceVo> getTree(Long parentId,Map<Long, List<AdminResource>> parentMap, String language) {
        List<TreeResourceVo> tree = new ArrayList<>();
        if(parentMap.get(parentId) != null) {
            for (AdminResource resource: parentMap.get(parentId)) {
                TreeResourceVo resourceVo = new TreeResourceVo();
                resourceVo.setKey(String.valueOf(resource.getId()));
                if(language.equals("en")) {
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
