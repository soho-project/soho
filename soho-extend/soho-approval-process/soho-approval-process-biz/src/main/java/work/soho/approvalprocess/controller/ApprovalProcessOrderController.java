package work.soho.approvalprocess.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.approvalprocess.domain.ApprovalProcess;
import work.soho.approvalprocess.domain.ApprovalProcessOrder;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderApplyStatusEnum;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderStatusEnum;
import work.soho.approvalprocess.dto.ApprovalProcessOrderMyListDto;
import work.soho.approvalprocess.request.ApprovalProcessOrderCreateRequest;
import work.soho.approvalprocess.request.ApprovalProcessOrderListRequest;
import work.soho.approvalprocess.request.ApprovalRequest;
import work.soho.approvalprocess.service.ApprovalProcessOrderNodeService;
import work.soho.approvalprocess.service.ApprovalProcessOrderService;
import work.soho.approvalprocess.service.ApprovalProcessService;
import work.soho.approvalprocess.vo.ApprovalProcessMetadataVo;
import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;
import work.soho.approvalprocess.vo.MyProvalProcessOrderVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/approvalProcessOrder")
@Api(tags = "审批单管理")
public class ApprovalProcessOrderController {
    private final ApprovalProcessService approvalProcessService;
    private final ApprovalProcessOrderService approvalProcessOrderService;
    private final ApprovalProcessOrderNodeService approvalProcessOrderNodeService;

    @ApiOperation("审批单列表")
    @GetMapping("list")
    public R<PageSerializable<MyProvalProcessOrderVo>> list(HttpServletRequest request, ApprovalProcessOrderListRequest approvalProcessOrderListRequest) {
        LambdaQueryWrapper<ApprovalProcessOrder> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(ApprovalProcessOrder::getId);
        lqw.like(approvalProcessOrderListRequest.getOutNo()!=null, ApprovalProcessOrder::getOutNo, approvalProcessOrderListRequest.getOutNo());
        if(approvalProcessOrderListRequest.getCreateTime()!=null) {
            lqw.between(approvalProcessOrderListRequest.getCreateTime()!=null, ApprovalProcessOrder::getCreatedTime, approvalProcessOrderListRequest.getCreateTime().get(0), approvalProcessOrderListRequest.getCreateTime().get(1));
        }
        PageHelper.startPage(request.getParameterMap());
        List<ApprovalProcessOrder> list = approvalProcessOrderService.list(lqw);
        return listToVo(list);
    }

    @ApiOperation("审批单列表")
    @GetMapping("myList")
    public R<PageSerializable<MyProvalProcessOrderVo>> myList(HttpServletRequest request, ApprovalProcessOrderListRequest approvalProcessOrderListRequest) {
        Long userId = SecurityUtils.getLoginUserId();
        ApprovalProcessOrderMyListDto orderMyListDto = new ApprovalProcessOrderMyListDto();
        if(approvalProcessOrderListRequest.getType() == null) {
            orderMyListDto.setUserId(userId);
            orderMyListDto.setApplyUserId(userId);
        } else if(approvalProcessOrderListRequest.getType() == 1) { //我申请的审批单
            orderMyListDto.setUserId(userId);
        } else if(approvalProcessOrderListRequest.getType() == 2) { //我已经审批的
            List<Integer> statusList = new ArrayList<>();
            statusList.add(ApprovalProcessOrderNodeApplyStatusEnum.AGREE.getStatus());
            statusList.add(ApprovalProcessOrderNodeApplyStatusEnum.REJECT.getStatus());
            statusList.add(ApprovalProcessOrderNodeApplyStatusEnum.PASS_ON.getStatus());
            orderMyListDto.setNodeListStatus(statusList);
            orderMyListDto.setUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 3) {  //审批人包含我的； 未审批也计算
            List<Integer> statusList = new ArrayList<>();
            statusList.add(ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus());
            orderMyListDto.setNodeListStatus(statusList);
            orderMyListDto.setUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 4) {  //审批人包含我的； 未审批也计算
            orderMyListDto.setApplyUserId(userId);
        }

        PageUtils.startPage();
        List<ApprovalProcessOrder> list = approvalProcessOrderService.myList(orderMyListDto);
        return listToVo(list);
    }

    @ApiOperation("对审批单节点进行审批")
    @PostMapping("approval")
    public R<Boolean> approval(@RequestBody ApprovalRequest approvalRequest) {
        ApprovalProcessOrderNode approvalProcessOrderNode = approvalProcessOrderNodeService.getById(approvalRequest.getId());
        approvalProcessOrderNode.setReply(approvalRequest.getReply());
        Assert.notNull(approvalProcessOrderNode, "没有找到对应的审批单信息");
        ApprovalProcessOrderNodeApplyStatusEnum approvalProcessOrderNodeApplyStatusEnum = ApprovalProcessOrderNodeApplyStatusEnum.getByStatus(approvalRequest.getStatus());
        approvalProcessOrderService.approve(SecurityUtils.getLoginUserId(), approvalProcessOrderNode,
                approvalProcessOrderNodeApplyStatusEnum, approvalRequest.getNextUserId());
        return R.success();
    }

    @ApiOperation("创建审批单")
    @PostMapping
    public R<ApprovalProcessOrder> create(@RequestBody ApprovalProcessOrderCreateRequest approvalProcessOrderCreateRequest) {
        ApprovalProcessOrderVo approvalProcessOrderVo = new ApprovalProcessOrderVo();
        approvalProcessOrderVo.setApprovalProcessId(approvalProcessOrderCreateRequest.getApprovalProcessId());
        ApprovalProcess approvalProcess = approvalProcessService.getById(approvalProcessOrderCreateRequest.getApprovalProcessId());
        Assert.notNull(approvalProcess, "没有找到对应的审批单");
        approvalProcessOrderVo.setApprovalProcessNo(approvalProcess.getNo());
        approvalProcessOrderVo.setApplyUserId(SecurityUtils.getLoginUserId());
        //获取元数据
        Map<String, ApprovalProcessMetadataVo> metadataVoMap = approvalProcessService.getMetadatas(approvalProcess.getMetadata());
        //解析content
        LinkedList<ApprovalProcessOrderVo.ContentItem> contentItems = new LinkedList<>();
        approvalProcessOrderCreateRequest.getContent().forEach((key, value)->{
            String keyName = key.toUpperCase(Locale.ROOT);
            ApprovalProcessOrderVo.ContentItem item = new ApprovalProcessOrderVo.ContentItem();
            ApprovalProcessMetadataVo approvalProcessMetadataVo = metadataVoMap.get(keyName);
            Assert.notNull(approvalProcessMetadataVo, "元数据有误， 请检查传递的元数据名：" + keyName);
            item.setKey(keyName);
            item.setContent(value);
            item.setTitle(approvalProcessMetadataVo.getTitle());
            contentItems.add(item);
        });
        approvalProcessOrderVo.setContentItemList(contentItems);
        if(StringUtils.isEmpty(approvalProcessOrderCreateRequest.getOutNo())) {
            approvalProcessOrderVo.setOutNo(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        } else {
            approvalProcessOrderVo.setOutNo(approvalProcessOrderCreateRequest.getOutNo());
        }
        return R.success(approvalProcessOrderService.create(approvalProcessOrderVo));
    }

    /**
     * 订单列表转换为vo结果
     *
     * @param list
     * @return
     */
    private R<PageSerializable<MyProvalProcessOrderVo>> listToVo(List<ApprovalProcessOrder> list) {
        //无数据
        if(list.size()==0) {
            return R.success(new PageSerializable<MyProvalProcessOrderVo>(new ArrayList<>()));
        }
        //处理node 信息
        List<Integer> orderIds = list.stream().map(ApprovalProcessOrder::getId).collect(Collectors.toList());
        List<Long> userIds = list.stream().map(ApprovalProcessOrder::getApplyUserId).collect(Collectors.toList());
        List<Integer> approvalProcessId = list.stream().map(ApprovalProcessOrder::getApprovalProcessId).collect(Collectors.toList());
        //对应节点信息
        List<ApprovalProcessOrderNode> nodes = approvalProcessOrderNodeService.list(new LambdaQueryWrapper<ApprovalProcessOrderNode>()
                .in(ApprovalProcessOrderNode::getOrderId, orderIds).orderByAsc(ApprovalProcessOrderNode::getSerialNumber));
        userIds.addAll(nodes.stream().map(ApprovalProcessOrderNode::getUserId).collect(Collectors.toList()));
        //对应审批流信息
        Map<Integer, ApprovalProcess> approvalProcessMap = approvalProcessService.list(new LambdaQueryWrapper<ApprovalProcess>().in(ApprovalProcess::getId, approvalProcessId))
                .stream().collect(Collectors.toMap(ApprovalProcess::getId, (item)->item));
        ;

        //聚合结果集
        List<MyProvalProcessOrderVo> voList = list.stream().map(item->{
            MyProvalProcessOrderVo myProvalProcessOrderVo = BeanUtils.copy(item, MyProvalProcessOrderVo.class);
            myProvalProcessOrderVo.setStatusName(ApprovalProcessOrderStatusEnum.getByStatus(myProvalProcessOrderVo.getStatus()).getName());
            myProvalProcessOrderVo.setApplyStatusName(ApprovalProcessOrderApplyStatusEnum.getByStatus(myProvalProcessOrderVo.getApplyStatus()).getName());
            myProvalProcessOrderVo.setApprovalProcessName(approvalProcessMap.get(myProvalProcessOrderVo.getApprovalProcessId()).getName());

            List<MyProvalProcessOrderVo.Node> myNodes = nodes.stream()
                    .filter(node-> node.getOrderId() == item.getId())
                    .map(node->{
                        MyProvalProcessOrderVo.Node newNode = BeanUtils.copy(node, MyProvalProcessOrderVo.Node.class);
                        newNode.setStatusName(ApprovalProcessOrderNodeApplyStatusEnum.getByStatus(node.getStatus()).getName());
                        return newNode;
                    }).collect(Collectors.toList());
            myProvalProcessOrderVo.setNodes(myNodes);
            return myProvalProcessOrderVo;
        }).collect(Collectors.toList());

        PageSerializable<MyProvalProcessOrderVo> pageSerializable = new PageSerializable<>(voList);
        pageSerializable.setTotal(((Page)list).getTotal());
        return R.success(pageSerializable);
    }
}
