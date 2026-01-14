package work.soho.approvalprocess.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.approvalprocess.biz.domain.ApprovalProcess;
import work.soho.approvalprocess.biz.domain.ApprovalProcessNode;
import work.soho.approvalprocess.biz.service.ApprovalProcessNodeService;
import work.soho.approvalprocess.biz.service.ApprovalProcessService;
import work.soho.approvalprocess.api.vo.ApprovalProcessVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.RequestUtil;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/approvalProcess")
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
        PageHelper.startPage(RequestUtil.getRequest().getParameterMap());
        List<ApprovalProcess> list = approvalProcessService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 审批流选项列表
     *
     * @return
     */
    @ApiOperation("审批流选项")
    @GetMapping("options")
    public R<List<ApprovalProcess>> options() {
        List<ApprovalProcess> list = approvalProcessService.list();
        return R.success(list);
    }

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
}
