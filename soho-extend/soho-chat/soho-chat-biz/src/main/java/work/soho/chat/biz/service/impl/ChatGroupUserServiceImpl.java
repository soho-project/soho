package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.mapper.ChatGroupUserMapper;
import work.soho.chat.biz.service.ChatGroupUserService;

@RequiredArgsConstructor
@Service
public class ChatGroupUserServiceImpl extends ServiceImpl<ChatGroupUserMapper, ChatGroupUser>
    implements ChatGroupUserService{

    /**
     * 获取指定用户组用户数据
     *
     * @param id
     * @param uid
     * @return
     */
    @Override
    public ChatGroupUser getByUid(Long id, Long uid) {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id)
                .eq(ChatGroupUser::getChatUid, uid);
        return getOne(lambdaQueryWrapper);
    }
}
