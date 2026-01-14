package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenTicketMessage;
import work.soho.open.biz.enums.OpenTicketMessageEnums;
import work.soho.open.biz.service.OpenTicketMessageService;

import java.util.Arrays;
import java.util.List;

;
/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openTicketMessage" )
public class UserOpenTicketMessageController {

    private final OpenTicketMessageService openTicketMessageService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openTicketMessage::list", name = "获取  列表")
    public R<PageSerializable<OpenTicketMessage>> list(OpenTicketMessage openTicketMessage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicketMessage> lqw = new LambdaQueryWrapper<OpenTicketMessage>();
        lqw.eq(openTicketMessage.getTicketId() != null, OpenTicketMessage::getTicketId ,openTicketMessage.getTicketId());
        lqw.eq(openTicketMessage.getId() != null, OpenTicketMessage::getId ,openTicketMessage.getId());
        lqw.like(StringUtils.isNotBlank(openTicketMessage.getContent()),OpenTicketMessage::getContent ,openTicketMessage.getContent());
        lqw.eq(openTicketMessage.getUserId() != null, OpenTicketMessage::getUserId ,openTicketMessage.getUserId());
        lqw.eq(openTicketMessage.getType() != null, OpenTicketMessage::getType ,openTicketMessage.getType());
        lqw.eq(openTicketMessage.getAdminId() != null, OpenTicketMessage::getAdminId ,openTicketMessage.getAdminId());
        lqw.eq(openTicketMessage.getUpdatedTime() != null, OpenTicketMessage::getUpdatedTime ,openTicketMessage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicketMessage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenTicketMessage> list = openTicketMessageService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openTicketMessage::getInfo", name = "获取  详细信息")
    public R<OpenTicketMessage> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openTicketMessageService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openTicketMessage::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenTicketMessage openTicketMessage, @AuthenticationPrincipal SohoUserDetails userDetails) {
        openTicketMessage.setUserId(userDetails.getId());
        openTicketMessage.setType(OpenTicketMessageEnums.Type.USER_MESSAGE.getId());
        return R.success(openTicketMessageService.save(openTicketMessage));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openTicketMessage::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenTicketMessage openTicketMessage) {
        return R.success(openTicketMessageService.updateById(openTicketMessage));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openTicketMessage::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openTicketMessageService.removeByIds(Arrays.asList(ids)));
    }
}