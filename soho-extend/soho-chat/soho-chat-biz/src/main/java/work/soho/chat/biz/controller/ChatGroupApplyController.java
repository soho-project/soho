package work.soho.chat.biz.controller;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatGroupApply;
import work.soho.chat.biz.service.ChatGroupApplyService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 群组申请Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatGroupApply" )
public class ChatGroupApplyController {

    private final ChatGroupApplyService chatGroupApplyService;

    /**
     * 查询群组申请列表
     */
    @GetMapping("/list")
    @Node(value = "chatGroupApply::list", name = "群组申请列表")
    public R<PageSerializable<ChatGroupApply>> list(ChatGroupApply chatGroupApply, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatGroupApply> lqw = new LambdaQueryWrapper<ChatGroupApply>();
        lqw.eq(chatGroupApply.getId() != null, ChatGroupApply::getId ,chatGroupApply.getId());
        lqw.eq(chatGroupApply.getChatUid() != null, ChatGroupApply::getChatUid ,chatGroupApply.getChatUid());
        lqw.eq(chatGroupApply.getGroupId() != null, ChatGroupApply::getGroupId ,chatGroupApply.getGroupId());
        lqw.eq(chatGroupApply.getStatus() != null, ChatGroupApply::getStatus ,chatGroupApply.getStatus());
        lqw.like(StringUtils.isNotBlank(chatGroupApply.getAsk()),ChatGroupApply::getAsk ,chatGroupApply.getAsk());
        lqw.like(StringUtils.isNotBlank(chatGroupApply.getAnswer()),ChatGroupApply::getAnswer ,chatGroupApply.getAnswer());
        lqw.like(StringUtils.isNotBlank(chatGroupApply.getApplyMessage()),ChatGroupApply::getApplyMessage ,chatGroupApply.getApplyMessage());
        lqw.eq(chatGroupApply.getUpdatedTime() != null, ChatGroupApply::getUpdatedTime ,chatGroupApply.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatGroupApply::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatGroupApply::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatGroupApply> list = chatGroupApplyService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取群组申请详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatGroupApply::getInfo", name = "群组申请详细信息")
    public R<ChatGroupApply> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatGroupApplyService.getById(id));
    }

    /**
     * 新增群组申请
     */
    @PostMapping
    @Node(value = "chatGroupApply::add", name = "群组申请新增")
    public R<Boolean> add(@RequestBody ChatGroupApply chatGroupApply) {
        return R.success(chatGroupApplyService.save(chatGroupApply));
    }

    /**
     * 修改群组申请
     */
    @PutMapping
    @Node(value = "chatGroupApply::edit", name = "群组申请修改")
    public R<Boolean> edit(@RequestBody ChatGroupApply chatGroupApply) {
        return R.success(chatGroupApplyService.updateById(chatGroupApply));
    }

    /**
     * 删除群组申请
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatGroupApply::remove", name = "群组申请删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatGroupApplyService.removeByIds(Arrays.asList(ids)));
    }
}