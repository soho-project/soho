package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.domain.ChatUserFriendQuestions;
import work.soho.chat.biz.enums.ChatUserEnums;
import work.soho.chat.biz.req.UpdateFriendAuthReq;
import work.soho.chat.biz.service.ChatUserFriendQuestionsService;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.UserQuestionsVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;

import java.time.LocalDateTime;

@Api(tags = "客户端好友认证")
@RestController
@RequestMapping("/chat/chat/chatUserFriendQuestions")
@RequiredArgsConstructor
public class ClientChatFriendQuestionsController {
    private final ChatUserService chatUserService;
    private final ChatUserFriendQuestionsService chatUserFriendQuestionsService;

    /**
     * 用户的问题列表
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<UserQuestionsVo> list(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        UserQuestionsVo userQuestionsVo = new UserQuestionsVo();
        ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());
        userQuestionsVo.setAuthType(chatUser.getAuthFriendType());
        LambdaQueryWrapper<ChatUserFriendQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriendQuestions::getUid, sohoUserDetails.getId());
        lambdaQueryWrapper.orderByAsc(ChatUserFriendQuestions::getId);
        chatUserFriendQuestionsService.list(lambdaQueryWrapper).forEach(item->{
            userQuestionsVo.getQuestionsList().add(BeanUtils.copy(item, UserQuestionsVo.Questions.class));
        });
        return R.success(userQuestionsVo);
    }

    /**
     * 获取好友认证方式以及问题
     *
     * @param id
     * @return
     */
    @GetMapping("/friend/{id}")
    public R<UserQuestionsVo> friend(@PathVariable Long id) {
        Assert.notNull(id, "请传递好友用户ID");
        UserQuestionsVo userQuestionsVo = new UserQuestionsVo();
        ChatUser chatUser = chatUserService.getById(id);
        Assert.notNull(chatUser, "用户不存在");

        userQuestionsVo.setAuthType(chatUser.getAuthFriendType());
        LambdaQueryWrapper<ChatUserFriendQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriendQuestions::getUid, id);
        lambdaQueryWrapper.orderByAsc(ChatUserFriendQuestions::getId);
        chatUserFriendQuestionsService.list(lambdaQueryWrapper).forEach(item->{
            userQuestionsVo.getQuestionsList().add(BeanUtils.copy(item, UserQuestionsVo.Questions.class));
        });
        return R.success(userQuestionsVo);
    }

    /**
     * 更新用户添加好友方式
     *
     * @param updateFriendAuthReq
     * @param sohoUserDetails
     * @return
     */
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> create(@RequestBody UpdateFriendAuthReq updateFriendAuthReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(updateFriendAuthReq.getAuthType(), "认证方式不能为空");
        ChatUser chatUser = chatUserService.getById(sohoUserDetails.getId());
        chatUser.setAuthFriendType(updateFriendAuthReq.getAuthType());
        chatUser.setUpdatedTime(LocalDateTime.now());
        chatUserService.updateById(chatUser);

        //检查判断问题是否为空; 需要回答问题的认证类型必须传递问题
        if(updateFriendAuthReq.getAuthType() == ChatUserEnums.AuthFriendType.ANSWER_QUESTIONS.getType() || updateFriendAuthReq.getAuthType() == ChatUserEnums.AuthFriendType.ANSWER_QUESTIONS_RECOGNITION.getType()) {
            if(updateFriendAuthReq.getQuestionsList() == null || updateFriendAuthReq.getQuestionsList().size() == 0) {
                throw new RuntimeException("没有设置提问问题");
            }
        }

        //清除历史提问问题
        LambdaQueryWrapper<ChatUserFriendQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriendQuestions::getUid, sohoUserDetails.getId());
        chatUserFriendQuestionsService.remove(lambdaQueryWrapper);

        //循环新增问题
        updateFriendAuthReq.getQuestionsList().forEach(item->{
            ChatUserFriendQuestions chatUserFriendQuestions = BeanUtils.copy(item, ChatUserFriendQuestions.class);
            chatUserFriendQuestions.setUid(sohoUserDetails.getId());
            chatUserFriendQuestions.setUpdatedTime(LocalDateTime.now());
            chatUserFriendQuestions.setCreatedTime(LocalDateTime.now());
            chatUserFriendQuestionsService.save(chatUserFriendQuestions);
        });

        return R.success(true);
    }
}
