package work.soho.open.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenTicket;
import work.soho.open.biz.domain.OpenTicketMessage;
import work.soho.open.biz.enums.OpenTicketEnums;
import work.soho.open.biz.enums.OpenTicketMessageEnums;
import work.soho.open.biz.service.OpenTicketMessageService;
import work.soho.open.biz.service.OpenTicketService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openTicketMessage" )
public class OpenTicketMessageController {

    private final OpenTicketMessageService openTicketMessageService;
    private final OpenTicketService openTicketService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openTicketMessage::list", name = "获取  列表")
    public R<PageSerializable<OpenTicketMessage>> list(OpenTicketMessage openTicketMessage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicketMessage> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openTicketMessage.getId() != null, OpenTicketMessage::getId ,openTicketMessage.getId());
        lqw.eq(openTicketMessage.getTicketId() != null, OpenTicketMessage::getTicketId ,openTicketMessage.getTicketId());
        lqw.like(StringUtils.isNotBlank(openTicketMessage.getContent()),OpenTicketMessage::getContent ,openTicketMessage.getContent());
        lqw.eq(openTicketMessage.getUserId() != null, OpenTicketMessage::getUserId ,openTicketMessage.getUserId());
        lqw.eq(openTicketMessage.getType() != null, OpenTicketMessage::getType ,openTicketMessage.getType());
        lqw.eq(openTicketMessage.getAdminId() != null, OpenTicketMessage::getAdminId ,openTicketMessage.getAdminId());
        lqw.eq(openTicketMessage.getUpdatedTime() != null, OpenTicketMessage::getUpdatedTime ,openTicketMessage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicketMessage::getId);
        List<OpenTicketMessage> list = openTicketMessageService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openTicketMessage::getInfo", name = "获取  详细信息")
    public R<OpenTicketMessage> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openTicketMessageService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openTicketMessage::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenTicketMessage openTicketMessage, @AuthenticationPrincipal SohoUserDetails userDetails) {
        openTicketMessage.setAdminId(userDetails.getId());
        Assert.notNull(openTicketMessage.getTicketId(), "请选择工单");
        Assert.notNull(openTicketMessage.getContent(), "请输入内容");
        OpenTicket ticket = openTicketService.getById(openTicketMessage.getTicketId());
        Assert.notNull(ticket, "工单不存在");
        Assert.isTrue(ticket.getStatus() != OpenTicketEnums.Status.CLOSED.getId(), "工单已关闭");

        ticket.setLastAnswerTime(LocalDateTime.now());
        ticket.setStatus(OpenTicketEnums.Status.REPLIED.getId());
        openTicketService.updateById(ticket);

        openTicketMessage.setUpdatedTime(LocalDateTime.now());
        openTicketMessage.setCreatedTime(LocalDateTime.now());
        openTicketMessage.setType(OpenTicketMessageEnums.Type.ADMINISTRATOR_MESSAGE.getId());

        return R.success(openTicketMessageService.save(openTicketMessage));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openTicketMessage::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenTicketMessage openTicketMessage) {
        return R.success(openTicketMessageService.updateById(openTicketMessage));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openTicketMessage::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openTicketMessageService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenTicketMessage.class)
    @Node(value = "openTicketMessage::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenTicketMessage openTicketMessage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenTicketMessage> lqw = new LambdaQueryWrapper<OpenTicketMessage>();
        lqw.eq(openTicketMessage.getId() != null, OpenTicketMessage::getId ,openTicketMessage.getId());
        lqw.eq(openTicketMessage.getTicketId() != null, OpenTicketMessage::getTicketId ,openTicketMessage.getTicketId());
        lqw.like(StringUtils.isNotBlank(openTicketMessage.getContent()),OpenTicketMessage::getContent ,openTicketMessage.getContent());
        lqw.eq(openTicketMessage.getUserId() != null, OpenTicketMessage::getUserId ,openTicketMessage.getUserId());
        lqw.eq(openTicketMessage.getType() != null, OpenTicketMessage::getType ,openTicketMessage.getType());
        lqw.eq(openTicketMessage.getAdminId() != null, OpenTicketMessage::getAdminId ,openTicketMessage.getAdminId());
        lqw.eq(openTicketMessage.getUpdatedTime() != null, OpenTicketMessage::getUpdatedTime ,openTicketMessage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicketMessage::getId);
        return openTicketMessageService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openTicketMessage::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenTicketMessage.class, new ReadListener<OpenTicketMessage>() {
                @Override
                public void invoke(OpenTicketMessage openTicketMessage, AnalysisContext analysisContext) {
                    openTicketMessageService.save(openTicketMessage);
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