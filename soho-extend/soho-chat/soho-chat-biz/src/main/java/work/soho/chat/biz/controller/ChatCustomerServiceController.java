package work.soho.chat.biz.controller;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatCustomerService;
import work.soho.chat.biz.service.ChatCustomerServiceService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 客服Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatCustomerService" )
public class ChatCustomerServiceController {

    private final ChatCustomerServiceService chatCustomerServiceService;

    /**
     * 查询客服列表
     */
    @GetMapping("/list")
    @Node(value = "chatCustomerService::list", name = "客服列表")
    public R<PageSerializable<ChatCustomerService>> list(ChatCustomerService chatCustomerService, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatCustomerService> lqw = new LambdaQueryWrapper<ChatCustomerService>();
        if (chatCustomerService.getId() != null){
            lqw.eq(ChatCustomerService::getId ,chatCustomerService.getId());
        }
        if (chatCustomerService.getUserId() != null){
            lqw.eq(ChatCustomerService::getUserId ,chatCustomerService.getUserId());
        }
        if (chatCustomerService.getStatus() != null){
            lqw.eq(ChatCustomerService::getStatus ,chatCustomerService.getStatus());
        }
        if (chatCustomerService.getUpdatedTime() != null){
            lqw.eq(ChatCustomerService::getUpdatedTime ,chatCustomerService.getUpdatedTime());
        }
        if (chatCustomerService.getCreatedTime() != null){
            lqw.eq(ChatCustomerService::getCreatedTime ,chatCustomerService.getCreatedTime());
        }

        if(betweenCreatedTimeRequest.getStartTime()!= null) {
            lqw.gt(ChatCustomerService::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        }
        if(betweenCreatedTimeRequest.getEndTime()!= null) {
            lqw.lt(ChatCustomerService::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        }

        List<ChatCustomerService> list = chatCustomerServiceService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取客服详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatCustomerService::getInfo", name = "客服详细信息")
    public R<ChatCustomerService> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatCustomerServiceService.getById(id));
    }

    /**
     * 新增客服
     */
    @PostMapping
    @Node(value = "chatCustomerService::add", name = "客服新增")
    public R<Boolean> add(@RequestBody ChatCustomerService chatCustomerService) {
        chatCustomerService.setCreatedTime(LocalDateTime.now());
        chatCustomerService.setUpdatedTime(LocalDateTime.now());
        return R.success(chatCustomerServiceService.save(chatCustomerService));
    }

    /**
     * 修改客服
     */
    @PutMapping
    @Node(value = "chatCustomerService::edit", name = "客服修改")
    public R<Boolean> edit(@RequestBody ChatCustomerService chatCustomerService) {
        chatCustomerService.setUpdatedTime(LocalDateTime.now());
        return R.success(chatCustomerServiceService.updateById(chatCustomerService));
    }

    /**
     * 删除客服
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatCustomerService::remove", name = "客服删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatCustomerServiceService.removeByIds(Arrays.asList(ids)));
    }
}
