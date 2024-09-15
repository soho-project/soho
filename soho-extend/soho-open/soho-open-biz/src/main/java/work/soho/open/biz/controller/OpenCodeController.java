package work.soho.open.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.open.biz.domain.OpenCode;
import work.soho.open.biz.service.OpenCodeService;

import java.util.Arrays;
import java.util.List;
/**
 * 授权码Controller
 *
 * @author fang
 */
@Api(tags = "授权码")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/openCode" )
public class OpenCodeController {

    private final OpenCodeService openCodeService;

    /**
     * 查询授权码列表
     */
    @GetMapping("/list")
    @Node(value = "openCode::list", name = "授权码;;列表")
    public R<PageSerializable<OpenCode>> list(OpenCode openCode, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenCode> lqw = new LambdaQueryWrapper<OpenCode>();
        lqw.eq(openCode.getId() != null, OpenCode::getId ,openCode.getId());
        lqw.eq(openCode.getAppId() != null, OpenCode::getAppId ,openCode.getAppId());
        lqw.like(StringUtils.isNotBlank(openCode.getCode()),OpenCode::getCode ,openCode.getCode());
        lqw.eq(openCode.getUpdatedTime() != null, OpenCode::getUpdatedTime ,openCode.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenCode::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenCode::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openCode.getIsLogin() != null, OpenCode::getIsLogin ,openCode.getIsLogin());
        lqw.eq(openCode.getUid() != null, OpenCode::getUid ,openCode.getUid());
        lqw.eq(openCode.getOriginUid() != null, OpenCode::getOriginUid ,openCode.getOriginUid());
        List<OpenCode> list = openCodeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取授权码详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openCode::getInfo", name = "授权码;;详细信息")
    public R<OpenCode> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openCodeService.getById(id));
    }

    /**
     * 新增授权码
     */
    @PostMapping
    @Node(value = "openCode::add", name = "授权码;;新增")
    public R<Boolean> add(@RequestBody OpenCode openCode) {
        return R.success(openCodeService.save(openCode));
    }

    /**
     * 修改授权码
     */
    @PutMapping
    @Node(value = "openCode::edit", name = "授权码;;修改")
    public R<Boolean> edit(@RequestBody OpenCode openCode) {
        return R.success(openCodeService.updateById(openCode));
    }

    /**
     * 删除授权码
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openCode::remove", name = "授权码;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openCodeService.removeByIds(Arrays.asList(ids)));
    }
}