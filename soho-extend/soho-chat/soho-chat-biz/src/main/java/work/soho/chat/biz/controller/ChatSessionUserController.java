package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
/**
 * 会话用户列表Controller
 *
 * @author fang
 */
@Api(tags = "会话用户")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatSessionUser" )
public class ChatSessionUserController {

    private final ChatSessionUserService chatSessionUserService;

    /**
     * 查询会话用户列表列表
     */
    @GetMapping("/list")
    @Node(value = "chatSessionUser::list", name = "会话用户列表列表")
    public R<PageSerializable<ChatSessionUser>> list(ChatSessionUser chatSessionUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatSessionUser> lqw = new LambdaQueryWrapper<ChatSessionUser>();
        if (chatSessionUser.getId() != null){
            lqw.eq(ChatSessionUser::getId ,chatSessionUser.getId());
        }
        if (chatSessionUser.getSessionId() != null){
            lqw.eq(ChatSessionUser::getSessionId ,chatSessionUser.getSessionId());
        }
        if (chatSessionUser.getUserId() != null){
            lqw.eq(ChatSessionUser::getUserId ,chatSessionUser.getUserId());
        }
        if (chatSessionUser.getUpdatedTime() != null){
            lqw.eq(ChatSessionUser::getUpdatedTime ,chatSessionUser.getUpdatedTime());
        }
        if (chatSessionUser.getCreatedTime() != null){
            lqw.eq(ChatSessionUser::getCreatedTime ,chatSessionUser.getCreatedTime());
        }

        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatSessionUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatSessionUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ChatSessionUser::getId);
        List<ChatSessionUser> list = chatSessionUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取会话用户列表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatSessionUser::getInfo", name = "会话用户列表详细信息")
    public R<ChatSessionUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatSessionUserService.getById(id));
    }

    /**
     * 新增会话用户列表
     */
    @PostMapping
    @Node(value = "chatSessionUser::add", name = "会话用户列表新增")
    public R<Boolean> add(@RequestBody ChatSessionUser chatSessionUser) {
        chatSessionUser.setCreatedTime(LocalDateTime.now());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        return R.success(chatSessionUserService.save(chatSessionUser));
    }

    /**
     * 修改会话用户列表
     */
    @PutMapping
    @Node(value = "chatSessionUser::edit", name = "会话用户列表修改")
    public R<Boolean> edit(@RequestBody ChatSessionUser chatSessionUser) {
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        return R.success(chatSessionUserService.updateById(chatSessionUser));
    }

    /**
     * 删除会话用户列表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatSessionUser::remove", name = "会话用户列表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatSessionUserService.removeByIds(Arrays.asList(ids)));
    }
}
