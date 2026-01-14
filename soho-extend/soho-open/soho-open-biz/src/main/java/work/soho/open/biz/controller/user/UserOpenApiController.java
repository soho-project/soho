package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.open.biz.domain.OpenApi;
import work.soho.open.biz.service.OpenApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;
/**
 * 开放平台apiController
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openApi" )
public class UserOpenApiController {

    private final OpenApiService openApiService;

    /**
     * 查询开放平台api列表
     */
    @GetMapping("/list")
    @Node(value = "user::openApi::list", name = "获取 开放平台api 列表")
    public R<PageSerializable<OpenApi>> list(OpenApi openApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApi> lqw = new LambdaQueryWrapper<OpenApi>();
        lqw.eq(openApi.getId() != null, OpenApi::getId ,openApi.getId());
        lqw.like(StringUtils.isNotBlank(openApi.getApiCode()),OpenApi::getApiCode ,openApi.getApiCode());
        lqw.like(StringUtils.isNotBlank(openApi.getApiName()),OpenApi::getApiName ,openApi.getApiName());
        lqw.like(StringUtils.isNotBlank(openApi.getMethod()),OpenApi::getMethod ,openApi.getMethod());
        lqw.like(StringUtils.isNotBlank(openApi.getPath()),OpenApi::getPath ,openApi.getPath());
        lqw.like(StringUtils.isNotBlank(openApi.getVersion()),OpenApi::getVersion ,openApi.getVersion());
        lqw.eq(openApi.getNeedAuth() != null, OpenApi::getNeedAuth ,openApi.getNeedAuth());
        lqw.eq(openApi.getStatus() != null, OpenApi::getStatus ,openApi.getStatus());
        lqw.like(StringUtils.isNotBlank(openApi.getDescription()),OpenApi::getDescription ,openApi.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApi.getUpdatedTime() != null, OpenApi::getUpdatedTime ,openApi.getUpdatedTime());
        List<OpenApi> list = openApiService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台api详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openApi::getInfo", name = "获取 开放平台api 详细信息")
    public R<OpenApi> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openApiService.getById(id));
    }

    /**
     * 新增开放平台api
     */
    @PostMapping
    @Node(value = "user::openApi::add", name = "新增 开放平台api")
    public R<Boolean> add(@RequestBody OpenApi openApi) {
        return R.success(openApiService.save(openApi));
    }

    /**
     * 修改开放平台api
     */
    @PutMapping
    @Node(value = "user::openApi::edit", name = "修改 开放平台api")
    public R<Boolean> edit(@RequestBody OpenApi openApi) {
        return R.success(openApiService.updateById(openApi));
    }

    /**
     * 删除开放平台api
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openApi::remove", name = "删除 开放平台api")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openApiService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该开放平台api 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "user::openApi::options", name = "获取 开放平台api 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OpenApi> list = openApiService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(OpenApi item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getApiName());
            options.add(optionVo);
        }
        return R.success(options);
    }
}