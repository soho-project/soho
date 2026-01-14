package work.soho.example.biz.controller.open;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.example.biz.domain.ExampleOption;
import work.soho.example.biz.service.ExampleOptionService;
import work.soho.open.api.annotation.OpenApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

/**
 * 样例选项Controller
 *
 * @author fang
 */
@Api(value = "open 样例选项", tags = "open 样例选项")
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/open/exampleOption" )
@OpenApi(value="ExampleOption", name="样例选项", description="null")
public class OpenExampleOptionController {

    private final ExampleOptionService exampleOptionService;

    /**
     * 查询样例选项列表
     */
    @GetMapping("/list")
    @Node(value = "open::exampleOption::list", name = "获取 样例选项 列表")
    @ApiOperation(value = "open 获取 样例选项 列表", notes = "open 获取 样例选项 列表")
    public R<PageSerializable<ExampleOption>> list(ExampleOption exampleOption, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleOption> lqw = new LambdaQueryWrapper<ExampleOption>();
        lqw.eq(exampleOption.getId() != null, ExampleOption::getId ,exampleOption.getId());
        lqw.like(StringUtils.isNotBlank(exampleOption.getKey()),ExampleOption::getKey ,exampleOption.getKey());
        lqw.like(StringUtils.isNotBlank(exampleOption.getValue()),ExampleOption::getValue ,exampleOption.getValue());
        lqw.eq(exampleOption.getUpdatedTime() != null, ExampleOption::getUpdatedTime ,exampleOption.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ExampleOption::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ExampleOption> list = exampleOptionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取样例选项详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "open::exampleOption::getInfo", name = "获取 样例选项 详细信息")
    @ApiOperation(value = "open 获取 样例选项 详细信息", notes = "open 获取 样例选项 详细信息")
    public R<ExampleOption> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ExampleOption exampleOption = exampleOptionService.getById(id);
        return R.success(exampleOption);
    }

    /**
     * 新增样例选项
     */
    @PostMapping
    @Node(value = "open::exampleOption::add", name = "新增 样例选项")
    @ApiOperation(value = "open 新增 样例选项", notes = "open 新增 样例选项")
    public R<Boolean> add(@RequestBody ExampleOption exampleOption, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(exampleOptionService.save(exampleOption));
    }

    /**
     * 修改样例选项
     */
    @PutMapping
    @Node(value = "open::exampleOption::edit", name = "修改 样例选项")
    @ApiOperation(value = "open 修改 样例选项", notes = "open 修改 样例选项")
    public R<Boolean> edit(@RequestBody ExampleOption exampleOption, @AuthenticationPrincipal SohoUserDetails userDetails) {
        ExampleOption oldExampleOption = exampleOptionService.getById(exampleOption.getId());
        Assert.notNull(oldExampleOption, "数据不存在");
        return R.success(exampleOptionService.updateById(exampleOption));
    }

    /**
     * 删除样例选项
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "open::exampleOption::remove", name = "删除 样例选项")
    @ApiOperation(value = "open 删除 样例选项", notes = "open 删除 样例选项")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(exampleOptionService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该样例选项 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "open::exampleOption::options", name = "获取 样例选项 选项")
    @ApiOperation(value = "open 获取 样例选项 选项", notes = "open 获取 样例选项 选项")
    public R<List<OptionVo<Integer, String>>> options(@AuthenticationPrincipal SohoUserDetails userDetails) {
        LambdaQueryWrapper<ExampleOption> lqw = new LambdaQueryWrapper<ExampleOption>();
        List<ExampleOption> list = exampleOptionService.list(lqw);
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ExampleOption item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getValue());
            options.add(optionVo);
        }
        return R.success(options);
    }
}