package work.soho.approvalprocess.biz.controller;

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
import work.soho.admin.api.vo.OptionVo;
import work.soho.approvalprocess.api.vo.ApprovalProcessVo;
import work.soho.approvalprocess.biz.domain.ApprovalProcess;
import work.soho.approvalprocess.biz.domain.ApprovalProcessNode;
import work.soho.approvalprocess.biz.service.ApprovalProcessNodeService;
import work.soho.approvalprocess.biz.service.ApprovalProcessService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import com.alibaba.excel.EasyExcelFactory;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/approvalProcess/admin/approvalProcess")
@Api(tags = "审批流管理")
public class ApprovalProcessController {
    private final ApprovalProcessService approvalProcessService;
    private final ApprovalProcessNodeService approvalProcessNodeService;

    @ApiOperation("审批流列表")
    @GetMapping("list")
    public R<PageSerializable<ApprovalProcess>> list(ApprovalProcess approvalProcess) {
        LambdaQueryWrapper<ApprovalProcess> lqw = new LambdaQueryWrapper<>();
        lqw.eq(approvalProcess.getId()!=null, ApprovalProcess::getId, approvalProcess.getId())
                .like(approvalProcess.getName()!=null, ApprovalProcess::getName, approvalProcess.getName())
                .eq(approvalProcess.getNo()!=null, ApprovalProcess::getNo, approvalProcess.getNo())
                .orderByDesc(ApprovalProcess::getId);
//        PageHelper.startPage(RequestUtil.getRequest().getParameterMap());
        PageUtils.startPage();
        List<ApprovalProcess> list = approvalProcessService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

//    /**
//     * 审批流选项列表
//     *
//     * @return
//     */
//    @ApiOperation("审批流选项")
//    @GetMapping("options")
//    public R<List<ApprovalProcess>> options() {
//        List<ApprovalProcess> list = approvalProcessService.list();
//        return R.success(list);
//    }

    @ApiOperation("审批流详情")
    @GetMapping("{id}")
    public R<ApprovalProcessVo> details(@PathVariable("id") Integer id) {
        ApprovalProcess approvalProcess = approvalProcessService.getById(id);
        if(approvalProcess == null) {
            return R.error("没有找到对应的审批流");
        }
        ApprovalProcessVo approvalProcessVo = BeanUtils.copy(approvalProcess, ApprovalProcessVo.class);
        //获取审批流节点详情
        List<ApprovalProcessNode> nodeList = approvalProcessNodeService.list(new LambdaQueryWrapper<ApprovalProcessNode>()
                .eq(ApprovalProcessNode::getApprovalProcessId, approvalProcess.getId()));
        approvalProcessVo.setNodes(BeanUtils.copyList(nodeList, ApprovalProcessVo.NodeVo.class));
        return R.success(approvalProcessVo);
    }

    @ApiOperation("创建审批流")
    @PostMapping
    public R<ApprovalProcess> create(@RequestBody ApprovalProcessVo approvalProcessVo) {
        ApprovalProcess approvalProcess = approvalProcessService.saveApprovalProcess(approvalProcessVo);
        return R.success(approvalProcess);
    }

    @ApiOperation("更新审批流")
    @PutMapping
    public R<ApprovalProcess> update(@RequestBody ApprovalProcessVo approvalProcessVo) {
        ApprovalProcess approvalProcess = approvalProcessService.saveApprovalProcess(approvalProcessVo);
        return R.success(approvalProcess);
    }

    /**
     * 获取该审批流 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "approvalProcess::options", name = "获取 审批流 选项")
    @ApiOperation(value = "获取 审批流 选项", notes = "获取 审批流 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ApprovalProcess> list = approvalProcessService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ApprovalProcess item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 审批流 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ApprovalProcess.class)
    @Node(value = "approvalProcess::exportExcel", name = "导出 审批流 Excel")
    @ApiOperation(value = "导出 审批流 Excel", notes = "导出 审批流 Excel")
    public Object exportExcel(ApprovalProcess approvalProcess, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ApprovalProcess> lqw = new LambdaQueryWrapper<ApprovalProcess>();
        lqw.eq(approvalProcess.getId() != null, ApprovalProcess::getId ,approvalProcess.getId());
        lqw.like(StringUtils.isNotBlank(approvalProcess.getNo()),ApprovalProcess::getNo ,approvalProcess.getNo());
        lqw.like(StringUtils.isNotBlank(approvalProcess.getName()),ApprovalProcess::getName ,approvalProcess.getName());
        lqw.eq(approvalProcess.getType() != null, ApprovalProcess::getType ,approvalProcess.getType());
        lqw.eq(approvalProcess.getEnable() != null, ApprovalProcess::getEnable ,approvalProcess.getEnable());
        lqw.eq(approvalProcess.getRejectAction() != null, ApprovalProcess::getRejectAction ,approvalProcess.getRejectAction());
        lqw.like(StringUtils.isNotBlank(approvalProcess.getMetadata()),ApprovalProcess::getMetadata ,approvalProcess.getMetadata());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ApprovalProcess::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ApprovalProcess::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ApprovalProcess::getId);
        return approvalProcessService.list(lqw);
    }

    /**
     * 导入 审批流 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "approvalProcess::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入 审批流 Excel", notes = "导入 审批流 Excel")
    public R importExcel(@RequestParam(value = "file") MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ApprovalProcess.class, new ReadListener<ApprovalProcess>() {
                @Override
                public void invoke(ApprovalProcess approvalProcess, AnalysisContext analysisContext) {
                    approvalProcessService.save(approvalProcess);
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
}
