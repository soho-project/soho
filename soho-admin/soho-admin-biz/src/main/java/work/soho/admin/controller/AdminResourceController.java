package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Node;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminResourceService;
import work.soho.api.admin.vo.RouteVo;
import work.soho.api.admin.vo.TreeResourceVo;
import work.soho.common.core.result.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return R.ok();
    }

    /**
     * 前端页面获取导航菜单信息
     *
     * @return
     */
    @GetMapping("/routes")
    public List<RouteVo> routeVoList() {
        LambdaQueryWrapper<AdminResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AdminResource::getType, 1);
        lqw.orderByAsc(AdminResource::getSort);
        List<AdminResource> list = adminResourceService.list(lqw);
        return list.stream().map(item->{
            RouteVo routeVo = new RouteVo();
            BeanUtils.copyProperties(item, routeVo);
            RouteVo.Langues zh = new RouteVo.Langues();
            zh.setName(item.getZhName());
            routeVo.setZh(zh);
            return routeVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取资源树
     *
     * TODO 根据登录用户进行过滤
     */
    public R<List<TreeResourceVo>> getResourceTree() {
        List<AdminResource> list = adminResourceService.list();
        Map<Long, List<AdminResource>> parentList = new HashMap<>();
        //构造parent -> son list
        for (AdminResource item: list) {
            Long parentId = item.getId();
            parentList.computeIfAbsent(parentId, k -> new ArrayList<AdminResource>());
            List<AdminResource> tmpParentList = parentList.get(parentId);
            tmpParentList.add(item);
        }

        //构造treevo
        return R.ok(getTree(Long.getLong("1"), parentList));
    }

    /**
     * 获取资源树
     *
     * @param parentId
     * @param parentMap
     * @return
     */
    private List<TreeResourceVo> getTree(Long parentId,Map<Long, List<AdminResource>> parentMap) {
        List<TreeResourceVo> tree = new ArrayList<>();
        if(parentMap.get(parentId) != null) {
            for (AdminResource resource: parentMap.get(parentId)) {
                TreeResourceVo resourceVo = new TreeResourceVo();
                resourceVo.setKey(String.valueOf(resource.getId()));
                resourceVo.setTitle(resource.getName());
                resourceVo.setKey(String.valueOf(resource.getId()));
                //递归查询子资源
                resourceVo.setChildren(getTree(resource.getId(), parentMap));
                tree.add(resourceVo);
            }
        }
        return tree;
    }
}
