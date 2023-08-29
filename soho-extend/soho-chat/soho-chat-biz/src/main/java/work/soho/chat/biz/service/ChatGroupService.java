package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.vo.ChatGroupVO;

import java.util.List;

public interface ChatGroupService extends IService<ChatGroup> {
    ChatGroup createGroup(ChatGroup chatGroup, List<Long> chatUserIds);

    void exitGroup(Long groupId,Long uid);

    void joinGroup(Long groupId,List<Long> uids);

    ChatGroupVO getDetails(Long id,Long uid);

    /**
     * 获取群组用户
     *
     * @param id
     * @param uid
     * @return
     */
    ChatGroupUser getChatGroupUser(Long id,Long uid);
}
