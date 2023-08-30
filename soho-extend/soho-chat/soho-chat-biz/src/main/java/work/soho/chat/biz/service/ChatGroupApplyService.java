package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatGroupApply;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatGroupApplyService extends IService<ChatGroupApply> {
    ChatGroupApply create(Long id, Long groupId, String anw,String answer,String applyMessage);
}
