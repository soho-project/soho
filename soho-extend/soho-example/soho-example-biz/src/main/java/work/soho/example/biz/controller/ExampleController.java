package work.soho.example.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.approvalprocess.api.service.ApprovalProcessOrderApiService;
import work.soho.approvalprocess.api.vo.ApprovalProcessOrderVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.service.ExampleService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 自动化样例Controller
 *
 * @author fang
 */
@Api(value="自动化样例",tags = "自动化样例")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/example/admin/example" )
public class ExampleController {

    private final ExampleService exampleService;
    private final AdminDictApiService adminDictApiService;
    private final ApprovalProcessOrderApiService approvalProcessOrderApiService;

    /**
     * 查询自动化样例列表
     */
    @GetMapping("/list")
    @Node(value = "example::list", name = "获取 自动化样例 列表")
    @ApiOperation(value = "获取 自动化样例 列表", notes = "获取 自动化样例 列表")
    public R<PageSerializable<Example>> list(Example example, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<Example> lqw = new LambdaQueryWrapper<>();
        lqw.eq(example.getId() != null, Example::getId ,example.getId());
        lqw.like(StringUtils.isNotBlank(example.getTitle()),Example::getTitle ,example.getTitle());
        lqw.eq(example.getCategoryId() != null, Example::getCategoryId ,example.getCategoryId());
        lqw.eq(example.getOptionId() != null, Example::getOptionId ,example.getOptionId());
        lqw.like(StringUtils.isNotBlank(example.getContent()),Example::getContent ,example.getContent());
        lqw.eq(example.getUpdatedTime() != null, Example::getUpdatedTime ,example.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(example.getStatus() != null, Example::getStatus ,example.getStatus());
        lqw.eq(example.getApplyStatus() != null, Example::getApplyStatus ,example.getApplyStatus());
        lqw.eq(example.getUserId() != null, Example::getUserId ,example.getUserId());
        lqw.eq(example.getOpenId() != null, Example::getOpenId ,example.getOpenId());
        lqw.eq(example.getAdminId() != null, Example::getAdminId ,example.getAdminId());
        lqw.eq(example.getDictInt() != null, Example::getDictInt ,example.getDictInt());
        lqw.like(StringUtils.isNotBlank(example.getDictString()),Example::getDictString ,example.getDictString());
        lqw.orderByDesc(Example::getId);
        List<Example> list = exampleService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "example::getInfo", name = "获取 自动化样例 详细信息")
    @ApiOperation(value = "获取 自动化样例 详细信息", notes = "获取 自动化样例 详细信息")
    public R<Example> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleService.getById(id));
    }

    /**
     * 新增自动化样例
     */
    @PostMapping
    @Node(value = "example::add", name = "新增 自动化样例")
    @ApiOperation(value = "新增 自动化样例", notes = "新增 自动化样例")
    public R<Boolean> add(@RequestBody Example example) {
        return R.success(exampleService.save(example));
    }

    /**
     * 修改自动化样例
     */
    @PutMapping
    @Node(value = "example::edit", name = "修改 自动化样例")
    @ApiOperation(value = "修改 自动化样例", notes = "修改 自动化样例")
    public R<Boolean> edit(@RequestBody Example example) {
        return R.success(exampleService.updateById(example));
    }

    /**
     * 删除自动化样例
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "example::remove", name = "删除 自动化样例")
    @ApiOperation(value = "删除 自动化样例", notes = "删除 自动化样例")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 申请审批 自动化样例
     */
    @PutMapping("apply")
    @Node(value = "example::apply", name = "申请审批 自动化样例")
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

    /**
     * 导出 自动化样例 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = Example.class)
    @Node(value = "example::exportExcel", name = "导出 自动化样例 Excel")
    @ApiOperation(value = "导出 自动化样例 Excel", notes = "导出 自动化样例 Excel")
    public Object exportExcel(Example example, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<Example> lqw = new LambdaQueryWrapper<Example>();
        lqw.eq(example.getId() != null, Example::getId ,example.getId());
        lqw.like(StringUtils.isNotBlank(example.getTitle()),Example::getTitle ,example.getTitle());
        lqw.eq(example.getCategoryId() != null, Example::getCategoryId ,example.getCategoryId());
        lqw.eq(example.getOptionId() != null, Example::getOptionId ,example.getOptionId());
        lqw.like(StringUtils.isNotBlank(example.getContent()),Example::getContent ,example.getContent());
        lqw.eq(example.getUpdatedTime() != null, Example::getUpdatedTime ,example.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(example.getStatus() != null, Example::getStatus ,example.getStatus());
        lqw.eq(example.getApplyStatus() != null, Example::getApplyStatus ,example.getApplyStatus());
        lqw.eq(example.getUserId() != null, Example::getUserId ,example.getUserId());
        lqw.eq(example.getOpenId() != null, Example::getOpenId ,example.getOpenId());
        lqw.eq(example.getAdminId() != null, Example::getAdminId ,example.getAdminId());
        lqw.eq(example.getDictInt() != null, Example::getDictInt ,example.getDictInt());
        lqw.like(StringUtils.isNotBlank(example.getDictString()),Example::getDictString ,example.getDictString());
        lqw.orderByDesc(Example::getId);
        return exampleService.list(lqw);
    }

    /**
     * 导入 自动化样例 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "example::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 自动化样例 Excel", notes = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), Example.class, new ReadListener<Example>() {
                @Override
                public void invoke(Example example, AnalysisContext analysisContext) {
                    exampleService.save(example);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取 自动化样例 字典整型 字典选项
     *
     * @return
     */
    @GetMapping("/queryDictIntOptions")
    @ApiOperation(value = "获取 自动化样例 字典整型 字典选项", notes = "获取 自动化样例 字典整型 字典选项")
    public R<List<OptionVo<Integer, String>>> queryDictIntOptions(){
        return R.success(adminDictApiService.getOptionsByCode("example-dict_int"));
    }
}