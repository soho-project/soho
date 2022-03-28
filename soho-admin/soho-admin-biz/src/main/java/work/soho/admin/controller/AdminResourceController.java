package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import work.soho.admin.annotation.Node;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminResourceService;
import work.soho.api.admin.vo.RouteVo;
import work.soho.common.core.result.R;

import java.util.List;
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
}
