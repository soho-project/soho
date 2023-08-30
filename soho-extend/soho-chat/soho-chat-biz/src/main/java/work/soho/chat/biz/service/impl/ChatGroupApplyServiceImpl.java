package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroupApply;
import work.soho.chat.biz.mapper.ChatGroupApplyMapper;
import work.soho.chat.biz.service.ChatGroupApplyService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatGroupApplyServiceImpl extends ServiceImpl<ChatGroupApplyMapper, ChatGroupApply>
    implements ChatGroupApplyService{

    /**
     * 创建群组申请
     *
     * @param chatUid
     * @param groupId
     * @param anw
     * @param answer
     * @param applyMessage
     * @return
     */
    public ChatGroupApply create(Long chatUid, Long groupId, String anw,String answer,String applyMessage) {
        //创建审批单
        ChatGroupApply chatGroupApply = new ChatGroupApply();
        chatGroupApply.setChatUid(chatUid);
        chatGroupApply.setGroupId(groupId);
        chatGroupApply.setAsk(anw);
        chatGroupApply.setAnswer(answer);
        chatGroupApply.setApplyMessage(applyMessage);
        chatGroupApply.setUpdatedTime(LocalDateTime.now());
        chatGroupApply.setCreatedTime(LocalDateTime.now());
        save(chatGroupApply);

        //创建发送通知
        return chatGroupApply;
    }
}
