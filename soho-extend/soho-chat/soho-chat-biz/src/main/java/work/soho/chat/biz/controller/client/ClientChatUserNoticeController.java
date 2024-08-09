package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatGroupApply;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.domain.ChatUserFriendApply;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.service.*;
import work.soho.chat.biz.vo.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "客户端聊天通知")
@RestController
@RequestMapping("/chat/chat/chatUserNotice")
@RequiredArgsConstructor
public class ClientChatUserNoticeController {

    private final ChatUserNoticeService chatUserNoticeService;

    private final ChatUserFriendApplyService chatUserFriendApplyService;

    private final ChatGroupApplyService chatGroupApplyService;

    private final ChatGroupService chatGroupService;

    private final ChatUserService chatUserService;

    /**
     * 通知列表
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<PageSerializable<NoticeVo>> list(ChatUserNotice chatUserNotice, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatUserNotice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //检查通知类型
        if(chatUserNotice != null && chatUserNotice.getType() != null) {
            lambdaQueryWrapper.eq(ChatUserNotice::getType, chatUserNotice.getType());
        }

        lambdaQueryWrapper.eq(ChatUserNotice::getChatUid, sohoUserDetails.getId());
        lambdaQueryWrapper.orderByDesc(ChatUserNotice::getUpdatedTime);
        lambdaQueryWrapper.orderByDesc(ChatUserNotice::getId);
        lambdaQueryWrapper.last("limit 100");
        List<ChatUserNotice> list = chatUserNoticeService.list(lambdaQueryWrapper);
        //TODO 获取好友类型通知
        Map<Long, FriendApplyVO> applyVOMap = fillApplyFriend(list, sohoUserDetails.getId());
        Map<Long, GroupApplyVO> groupApplyVOMap = getChatGroupApply(list, sohoUserDetails.getId());
        List<NoticeVo> result = list.stream().map(item->{
            NoticeVo noticeVo = BeanUtils.copy(item, NoticeVo.class);
            if(item.getType() == ChatUserNoticeEnums.Type.FRIEND.getType()) {
                //私聊申请
                noticeVo.setTarget(applyVOMap.get(item.getTrackingId()));
            } else {
                //群聊申请
                noticeVo.setTarget(groupApplyVOMap.get(item.getTrackingId()));
            }
            return noticeVo;
        }).collect(Collectors.toList());
        return R.success(new PageSerializable<>(result));
    }

    /**
     * 填充好友信息
     *
     * @param list
     */
    public Map<Long, FriendApplyVO> fillApplyFriend(List<ChatUserNotice> list, Long uid) {
        List<NoticeVo> resultList = new ArrayList<>();

        //获取好友申请相关信息
        List<Long> applyFriendIds = list.stream().filter(item->item.getType()== ChatUserNoticeEnums.Type.FRIEND.getType()).map(ChatUserNotice::getTrackingId).collect(Collectors.toList());
        if(applyFriendIds.isEmpty()) {
            return new HashMap<>();
        }
        List<ChatUserFriendApply> applyFriendList = chatUserFriendApplyService.listByIds(applyFriendIds);
        //查找好友申请信息
        Map<Long, ChatUserFriendApply> applyMap = applyFriendList.stream().collect(Collectors.toMap(ChatUserFriendApply::getId, item->item));
        //获取目标用户ID
        ArrayList<Long> uids = new ArrayList<>();
        applyFriendList.forEach(item->{
            if(item.getChatUid().equals(uid)) { //发送用户ID
                uids.add(item.getFriendUid());
            } else {
                uids.add(item.getChatUid());
            }
        });
        //查找用户信息
        Map<Long, BaseUserVO> userVOMap = chatUserService.listByIds(uids).stream().map(item-> {return BeanUtils.copy(item, BaseUserVO.class);}).collect(Collectors.toMap(BaseUserVO::getId, item->item));
        Map<Long, FriendApplyVO> friendApplyVOMap = applyFriendList.stream().map(item->{
            FriendApplyVO friendApplyVO = BeanUtils.copy(item, FriendApplyVO.class);
            friendApplyVO.setTargetUser(userVOMap.get(item.getChatUid().equals(uid) ? item.getFriendUid() : item.getChatUid()));
            return friendApplyVO;
        }).collect(Collectors.toMap(FriendApplyVO::getId, item->item));

        return friendApplyVOMap;
    }

    /**
     * 获取群组申请信息
     *
     * @return
     */
    public Map<Long, GroupApplyVO> getChatGroupApply(List<ChatUserNotice> list, Long uid) {
        //获取好友申请相关信息
        List<Long> groupApplyIds = list.stream().filter(item->item.getType()== ChatUserNoticeEnums.Type.GROUP.getType()).map(ChatUserNotice::getTrackingId).collect(Collectors.toList());
        if(groupApplyIds.isEmpty()) {
            return new HashMap<>();
        }

        //获取申请单列表
        List<ChatGroupApply> groupApplies = chatGroupApplyService.getBaseMapper().selectBatchIds(groupApplyIds);

        //获取群组申请单信息
        List<ChatGroupApply> chatApplyList =  chatGroupApplyService.getBaseMapper().selectBatchIds(groupApplyIds);
        //目标群
        List<Long> groupIds = chatApplyList.stream().map(ChatGroupApply::getGroupId).collect(Collectors.toList());
        Map<Long, GroupVO> groupVOMap = chatGroupService.getBaseMapper().selectBatchIds(groupIds).stream().map(item->{
            GroupVO groupVo = BeanUtils.copy(item, GroupVO.class);
            //TODO 计算是否已经在群里面
            return groupVo;
        }).collect(Collectors.toMap(GroupVO::getId, item->item));
        //获取相关用户
        List<Long> applyDoUids = chatApplyList.stream().map(ChatGroupApply::getApplyDoUid).collect(Collectors.toList());
        List<Long> uids = chatApplyList.stream().map(ChatGroupApply::getChatUid).collect(Collectors.toList());
        List<Long> chatUserIds = new ArrayList<>();
        chatUserIds.addAll(uids);
        chatUserIds.addAll(applyDoUids);
        Map<Long, BaseUserVO> chatUsers = chatUserService.getBaseMapper().selectBatchIds(chatUserIds).stream().collect(Collectors.toMap(ChatUser::getId, item->{
            return BeanUtils.copy(item, BaseUserVO.class);
        }));

        return groupApplies.stream().map(item->{
            GroupApplyVO groupApplyVO = BeanUtils.copy(item, GroupApplyVO.class);
            groupApplyVO.setGroupVO(groupVOMap.get(item.getGroupId()));
            groupApplyVO.setTargetUser(chatUsers.get(item.getChatUid()));
            groupApplyVO.setDoUser(chatUsers.get(item.getApplyDoUid()));
            return groupApplyVO;
        }).collect(Collectors.toMap(GroupApplyVO::getId, item->item));

    }
}
