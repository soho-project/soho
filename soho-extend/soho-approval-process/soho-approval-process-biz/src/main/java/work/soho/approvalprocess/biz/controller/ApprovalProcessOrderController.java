package work.soho.approvalprocess.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.approvalprocess.api.dto.ApprovalProcessOrderMyListDto;
import work.soho.approvalprocess.api.vo.ApprovalProcessMetadataVo;
import work.soho.approvalprocess.api.vo.ApprovalProcessOrderVo;
import work.soho.approvalprocess.api.vo.MyProvalProcessOrderVo;
import work.soho.approvalprocess.biz.domain.ApprovalProcess;
import work.soho.approvalprocess.biz.domain.ApprovalProcessOrder;
import work.soho.approvalprocess.biz.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.biz.domain.enums.ApprovalProcessOrderApplyStatusEnum;
import work.soho.approvalprocess.biz.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.biz.domain.enums.ApprovalProcessOrderStatusEnum;
import work.soho.approvalprocess.biz.request.ApprovalProcessOrderCreateRequest;
import work.soho.approvalprocess.biz.request.ApprovalProcessOrderListRequest;
import work.soho.approvalprocess.biz.request.ApprovalRequest;
import work.soho.approvalprocess.biz.service.ApprovalProcessOrderNodeService;
import work.soho.approvalprocess.biz.service.ApprovalProcessOrderService;
import work.soho.approvalprocess.biz.service.ApprovalProcessService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.utils.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvalProcess/admin/approvalProcessOrder")
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
        lqw.like(approvalProcessOrderListRequest.getOutNo() != null, ApprovalProcessOrder::getOutNo, approvalProcessOrderListRequest.getOutNo());
        if (approvalProcessOrderListRequest.getCreateTime() != null) {
            lqw.between(true, ApprovalProcessOrder::getCreatedTime,
                    approvalProcessOrderListRequest.getCreateTime().get(0),
                    approvalProcessOrderListRequest.getCreateTime().get(1));
        }
        PageUtils.startPage();
        List<ApprovalProcessOrder> list = approvalProcessOrderService.list(lqw);
        return listToVo(list);
    }

    @ApiOperation("审批单详情")
    @GetMapping("/{id}")
    public R<MyProvalProcessOrderVo> detail(@PathVariable Integer id) {
        ApprovalProcessOrder order = approvalProcessOrderService.getById(id);
        Assert.notNull(order, "没有找到对应的审批单信息");
        return orderToVo(order);
    }

    @ApiOperation("审批单列表")
    @GetMapping("/myList")
    public R<PageSerializable<MyProvalProcessOrderVo>> myList(HttpServletRequest request, ApprovalProcessOrderListRequest approvalProcessOrderListRequest) {
        Long userId = SecurityUtils.getLoginUserId();
        ApprovalProcessOrderMyListDto orderMyListDto = new ApprovalProcessOrderMyListDto();

        if (approvalProcessOrderListRequest.getType() == null) {
            // 所有我的相关：我发起的 + 我需要审批的（你原先逻辑就是同时设置 userId + applyUserId）
            orderMyListDto.setUserId(userId);
            orderMyListDto.setApplyUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 1) { // 我申请的审批单
            orderMyListDto.setUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 2) { // 我已经审批的
            List<Integer> statusList = Arrays.asList(
                    ApprovalProcessOrderNodeApplyStatusEnum.AGREE.getStatus(),
                    ApprovalProcessOrderNodeApplyStatusEnum.REJECT.getStatus(),
                    ApprovalProcessOrderNodeApplyStatusEnum.PASS_ON.getStatus()
            );
            orderMyListDto.setNodeListStatus(statusList);
            orderMyListDto.setUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 3) { // 审批人包含我的（未审批也算）
            List<Integer> statusList = Collections.singletonList(
                    ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus()
            );
            orderMyListDto.setNodeListStatus(statusList);
            orderMyListDto.setUserId(userId);
        } else if (approvalProcessOrderListRequest.getType() == 4) { // 我参与的（你原逻辑是按 applyUserId 过滤）
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
        Assert.notNull(approvalProcessOrderNode, "没有找到对应的审批单信息");

        approvalProcessOrderNode.setReply(approvalRequest.getReply());
        ApprovalProcessOrderNodeApplyStatusEnum applyStatusEnum =
                ApprovalProcessOrderNodeApplyStatusEnum.getByStatus(approvalRequest.getStatus());

        approvalProcessOrderService.approve(
                SecurityUtils.getLoginUserId(),
                approvalProcessOrderNode,
                applyStatusEnum,
                approvalRequest.getNextUserId()
        );
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

        // 获取元数据
        Map<String, ApprovalProcessMetadataVo> metadataVoMap = approvalProcessService.getMetadatas(approvalProcess.getMetadata());

        // 解析 content
        LinkedList<ApprovalProcessOrderVo.ContentItem> contentItems = new LinkedList<>();
        approvalProcessOrderCreateRequest.getContent().forEach((key, value) -> {
            String keyName = key.toUpperCase(Locale.ROOT);
            ApprovalProcessOrderVo.ContentItem item = new ApprovalProcessOrderVo.ContentItem();

            ApprovalProcessMetadataVo metadataVo = metadataVoMap.get(keyName);
            Assert.notNull(metadataVo, "元数据有误， 请检查传递的元数据名：" + keyName);

            item.setKey(keyName);
            item.setContent(value);
            item.setTitle(metadataVo.getTitle());
            contentItems.add(item);
        });

        approvalProcessOrderVo.setContentItemList(contentItems);

        if (StringUtils.isEmpty(approvalProcessOrderCreateRequest.getOutNo())) {
            approvalProcessOrderVo.setOutNo(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        } else {
            approvalProcessOrderVo.setOutNo(approvalProcessOrderCreateRequest.getOutNo());
        }

        return R.success(approvalProcessOrderService.create(approvalProcessOrderVo));
    }

    /**
     * 订单列表转换为 vo（列表页）
     */
    private R<PageSerializable<MyProvalProcessOrderVo>> listToVo(List<ApprovalProcessOrder> list) {
        if (list == null || list.isEmpty()) {
            return R.success(new PageSerializable<>(new ArrayList<>()));
        }

        // 订单ID
        List<Integer> orderIds = list.stream().map(ApprovalProcessOrder::getId).collect(Collectors.toList());
        // 审批流ID
        List<Integer> approvalProcessIds = list.stream().map(ApprovalProcessOrder::getApprovalProcessId).collect(Collectors.toList());

        // 一次性查出所有节点（避免 N+1）
        List<ApprovalProcessOrderNode> allNodes = approvalProcessOrderNodeService.list(
                new LambdaQueryWrapper<ApprovalProcessOrderNode>()
                        .in(ApprovalProcessOrderNode::getOrderId, orderIds)
                        .orderByAsc(ApprovalProcessOrderNode::getSerialNumber)
        );

        // 节点按订单分组（性能优化）
        Map<Integer, List<ApprovalProcessOrderNode>> nodesByOrderId = allNodes.stream()
                .collect(Collectors.groupingBy(ApprovalProcessOrderNode::getOrderId));

        // 审批流 map（避免重复查）
        Map<Integer, ApprovalProcess> approvalProcessMap = approvalProcessService
                .list(new LambdaQueryWrapper<ApprovalProcess>().in(ApprovalProcess::getId, approvalProcessIds))
                .stream()
                .collect(Collectors.toMap(ApprovalProcess::getId, item -> item, (a, b) -> a));

        // 组装 VO
        List<MyProvalProcessOrderVo> voList = list.stream().map(order -> toVo(order, approvalProcessMap, nodesByOrderId)).collect(Collectors.toList());

        PageSerializable<MyProvalProcessOrderVo> pageSerializable = new PageSerializable<>(voList);
        pageSerializable.setTotal(((Page) list).getTotal());
        return R.success(pageSerializable);
    }

    /**
     * 单个订单转换为 vo（详情页）
     */
    private R<MyProvalProcessOrderVo> orderToVo(ApprovalProcessOrder order) {
        List<ApprovalProcessOrderNode> nodes = approvalProcessOrderNodeService.list(
                new LambdaQueryWrapper<ApprovalProcessOrderNode>()
                        .eq(ApprovalProcessOrderNode::getOrderId, order.getId())
                        .orderByAsc(ApprovalProcessOrderNode::getSerialNumber)
        );

        Map<Integer, List<ApprovalProcessOrderNode>> nodesByOrderId = new HashMap<>();
        nodesByOrderId.put(order.getId(), nodes);

        ApprovalProcess approvalProcess = approvalProcessService.getById(order.getApprovalProcessId());
        Map<Integer, ApprovalProcess> approvalProcessMap = new HashMap<>();
        if (approvalProcess != null) {
            approvalProcessMap.put(approvalProcess.getId(), approvalProcess);
        }

        return R.success(toVo(order, approvalProcessMap, nodesByOrderId));
    }

    /**
     * 核心转换逻辑：ApprovalProcessOrder -> MyProvalProcessOrderVo
     */
    private MyProvalProcessOrderVo toVo(
            ApprovalProcessOrder order,
            Map<Integer, ApprovalProcess> approvalProcessMap,
            Map<Integer, List<ApprovalProcessOrderNode>> nodesByOrderId
    ) {
        MyProvalProcessOrderVo vo = BeanUtils.copy(order, MyProvalProcessOrderVo.class);

        if (vo.getStatus() != null) {
            ApprovalProcessOrderStatusEnum st = ApprovalProcessOrderStatusEnum.getByStatus(vo.getStatus());
            if (st != null) vo.setStatusName(st.getName());
        }
        if (vo.getApplyStatus() != null) {
            ApprovalProcessOrderApplyStatusEnum ast = ApprovalProcessOrderApplyStatusEnum.getByStatus(vo.getApplyStatus());
            if (ast != null) vo.setApplyStatusName(ast.getName());
        }

        ApprovalProcess ap = approvalProcessMap.get(vo.getApprovalProcessId());
        if (ap != null) {
            vo.setApprovalProcessName(ap.getName());
        }

        List<ApprovalProcessOrderNode> nodes = nodesByOrderId.getOrDefault(order.getId(), Collections.emptyList());
        List<MyProvalProcessOrderVo.Node> myNodes = nodes.stream()
                .map(node -> {
                    MyProvalProcessOrderVo.Node newNode = BeanUtils.copy(node, MyProvalProcessOrderVo.Node.class);
                    ApprovalProcessOrderNodeApplyStatusEnum ns = ApprovalProcessOrderNodeApplyStatusEnum.getByStatus(node.getStatus());
                    if (ns != null) newNode.setStatusName(ns.getName());
                    return newNode;
                })
                .collect(Collectors.toList());

        vo.setNodes(myNodes);
        return vo;
    }
}
