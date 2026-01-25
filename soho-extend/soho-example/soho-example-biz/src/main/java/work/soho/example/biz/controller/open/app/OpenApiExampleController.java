package work.soho.example.biz.controller.open.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.approvalprocess.api.service.ApprovalProcessOrderApiService;
import work.soho.approvalprocess.api.vo.ApprovalProcessOrderVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.service.ExampleService;
import work.soho.open.api.annotation.OpenApi;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 自动化样例Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/open/app" )
@Api(value = "自动化样例", tags = "自动化样例")
public class OpenApiExampleController {

    private final ExampleService exampleService;

    private final ApprovalProcessOrderApiService approvalProcessOrderApiService;
    /**
     * 查询自动化样例列表
     */
    @GetMapping("/list")
    @OpenApi(value = "open::example::list", name = "获取 自动化样例 列表")
    @ApiOperation(value = "查询自动化样例列表", notes = "查询自动化样例列表")
    public R<PageSerializable<Example>> list(Example example, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<Example> lqw = new LambdaQueryWrapper<Example>();
        lqw.eq(example.getId() != null, Example::getId ,example.getId());
        lqw.like(StringUtils.isNotBlank(example.getTitle()),Example::getTitle ,example.getTitle());
        lqw.eq(example.getCategoryId() != null, Example::getCategoryId ,example.getCategoryId());
        lqw.eq(example.getOptionId() != null, Example::getOptionId ,example.getOptionId());
        lqw.like(StringUtils.isNotBlank(example.getContent()),Example::getContent ,example.getContent());
        lqw.eq(example.getUpdatedTime() != null, Example::getUpdatedTime ,example.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(example.getApplyStatus() != null, Example::getApplyStatus ,example.getApplyStatus());
        lqw.eq(example.getStatus() != null, Example::getStatus ,example.getStatus());
        lqw.eq(example.getUserId() != null, Example::getUserId ,example.getUserId());
        lqw.eq(example.getOpenId() != null, Example::getOpenId ,example.getOpenId());
        lqw.eq(example.getDictInt() != null, Example::getDictInt ,example.getDictInt());
        List<Example> list = exampleService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例详细信息
     */
    @GetMapping(value = "/{id}" )
    @OpenApi(value = "open::example::getInfo", name = "获取 自动化样例 详细信息")
    @ApiOperation(value = "获取自动化样例详细信息", notes = "获取自动化样例详细信息")
    public R<Example> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleService.getById(id));
    }

    /**
     * 新增自动化样例
     */
    @PostMapping
    @OpenApi(value = "open::example::add", name = "新增 自动化样例")
    @ApiOperation(value = "新增自动化样例", notes = "新增自动化样例")
    public R<Boolean> add(@RequestBody Example example) {
        return R.success(exampleService.save(example));
    }

    /**
     * 修改自动化样例
     */
    @PutMapping
    @OpenApi(value = "open::example::edit", name = "修改 自动化样例")
    @ApiOperation(value = "修改自动化样例", notes = "修改自动化样例")
    public R<Boolean> edit(@RequestBody Example example) {
        return R.success(exampleService.updateById(example));
    }

    /**
     * 删除自动化样例
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "open::example::remove", name = "删除 自动化样例")
    @ApiOperation(value = "删除自动化样例", notes = "删除自动化样例")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 申请审批 自动化样例
     */
    @PutMapping("apply")
    @Node(value = "open::example::apply", name = "申请审批 自动化样例")
    @ApiOperation(value = "申请审批 自动化样例", notes = "申请审批 自动化样例")
    public R<Boolean> apply(@RequestBody Example example) {
        try {
            example.setUpdatedTime(LocalDateTime.now());
            //exampleService.updateById(example);

            ApprovalProcessOrderVo vo  = new ApprovalProcessOrderVo();
            vo.setApplyUserId(SecurityUtils.getLoginUserId());
            vo.setOutNo(example.getId().toString());
            vo.setApprovalProcessNo("4");
            vo.setCreatedTime(LocalDateTime.now());
            ApprovalProcessOrderVo.ContentItem itemTitle = new ApprovalProcessOrderVo.ContentItem();
            itemTitle.setKey("TITLE");
            itemTitle.setContent(example.getTitle());
            vo.getContentItemList().add(itemTitle);
            ApprovalProcessOrderVo.ContentItem itemContent = new ApprovalProcessOrderVo.ContentItem();
            itemContent.setKey("CONTENT");
            itemContent.setContent(example.getContent());
            vo.getContentItemList().add(itemContent);

            approvalProcessOrderApiService.push(vo);
            return R.success();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}