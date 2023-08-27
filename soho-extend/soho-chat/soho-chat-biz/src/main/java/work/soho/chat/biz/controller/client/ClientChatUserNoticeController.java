package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.domain.ChatUserFriendApply;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.service.ChatUserFriendApplyService;
import work.soho.chat.biz.service.ChatUserNoticeService;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.BaseUserVO;
import work.soho.chat.biz.vo.FriendApplyVO;
import work.soho.chat.biz.vo.NoticeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/chat/chatUserNotice")
@RequiredArgsConstructor
public class ClientChatUserNoticeController {

    private final ChatUserNoticeService chatUserNoticeService;

    private final ChatUserFriendApplyService chatUserFriendApplyService;

    private final ChatUserService chatUserService;

    /**
     * 通知列表
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<PageSerializable<NoticeVo>> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatUserNotice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserNotice::getChatUid, sohoUserDetails.getId());
        lambdaQueryWrapper.orderByDesc(ChatUserNotice::getUpdatedTime);
        lambdaQueryWrapper.orderByDesc(ChatUserNotice::getId);
        lambdaQueryWrapper.last("limit 100");
        List<ChatUserNotice> list = chatUserNoticeService.list(lambdaQueryWrapper);
        //TODO 获取好友类型通知
        Map<Long, FriendApplyVO> applyVOMap = fillApplyFriend(list, sohoUserDetails.getId());
        List<NoticeVo> result = list.stream().map(item->{
            NoticeVo noticeVo = BeanUtils.copy(item, NoticeVo.class);
            noticeVo.setTarget(applyVOMap.get(item.getTrackingId()));
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
        List<Long> applyFriendIds = list.stream().filter(item->item.getType()== ChatUserNoticeEnums.Type.FRIEND.getType()).map(ChatUserNotice::getTrackingId).collect(Collectors.toList());
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
}
