package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenTicket;
import work.soho.open.biz.service.OpenTicketService;

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
@RequestMapping("/open/user/openTicket" )
public class UserOpenTicketController {

    private final OpenTicketService openTicketService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openTicket::list", name = "获取  列表")
    public R<PageSerializable<OpenTicket>> list(OpenTicket openTicket, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicket> lqw = new LambdaQueryWrapper<OpenTicket>();
        lqw.eq(openTicket.getId() != null, OpenTicket::getId ,openTicket.getId());
        lqw.like(StringUtils.isNotBlank(openTicket.getNo()),OpenTicket::getNo ,openTicket.getNo());
        lqw.eq(openTicket.getCategoryId() != null, OpenTicket::getCategoryId ,openTicket.getCategoryId());
        lqw.like(StringUtils.isNotBlank(openTicket.getTitle()),OpenTicket::getTitle ,openTicket.getTitle());
        lqw.like(StringUtils.isNotBlank(openTicket.getContent()),OpenTicket::getContent ,openTicket.getContent());
        lqw.eq(openTicket.getStatus() != null, OpenTicket::getStatus ,openTicket.getStatus());
        lqw.eq(openTicket.getAppId() != null, OpenTicket::getAppId ,openTicket.getAppId());
        lqw.eq(openTicket.getLastReadTime() != null, OpenTicket::getLastReadTime ,openTicket.getLastReadTime());
        lqw.eq(openTicket.getLastAnswerTime() != null, OpenTicket::getLastAnswerTime ,openTicket.getLastAnswerTime());
        lqw.eq(openTicket.getUpdatedTime() != null, OpenTicket::getUpdatedTime ,openTicket.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        System.out.println("dubg===========0");
        System.out.println(userDetails.getId());
        lqw.eq(OpenTicket::getUserId, userDetails.getId());
        List<OpenTicket> list = openTicketService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openTicket::getInfo", name = "获取  详细信息")
    public R<OpenTicket> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenTicket openTicket = openTicketService.getById(id);
        if (!openTicket.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(openTicket);
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openTicket::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenTicket openTicket, @AuthenticationPrincipal SohoUserDetails userDetails) {
        openTicket.setUserId(userDetails.getId());
        openTicket.setNo(IDGeneratorUtils.snowflake().toString());
        return R.success(openTicketService.save(openTicket));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openTicket::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenTicket openTicket, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenTicket oldOpenTicket = openTicketService.getById(openTicket.getId());
        Assert.notNull(oldOpenTicket, "数据不存在");
        if(!oldOpenTicket.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(openTicketService.updateById(openTicket));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openTicket::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<OpenTicket> oldList = openTicketService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(OpenTicket item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(openTicketService.removeByIds(Arrays.asList(ids)));
    }
}