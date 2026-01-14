package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.*;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenAuthApplyRecords;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.enums.OpenAuthApplyRecordsEnums;
import work.soho.open.biz.request.AuthRequest;
import work.soho.open.biz.service.OpenAuthApplyRecordsService;
import work.soho.open.biz.service.OpenUserEnterpriseAuthService;
import work.soho.open.biz.service.OpenUserPersonalAuthService;
import work.soho.open.biz.service.OpenUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

;

/**
 * 认证申请记录表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openAuthApplyRecords" )
public class UserOpenAuthApplyRecordsController {

    private final OpenAuthApplyRecordsService openAuthApplyRecordsService;
    private final OpenUserPersonalAuthService openUserPersonalAuthService;
    private final OpenUserEnterpriseAuthService openUserEnterpriseAuthService;
    private final OpenUserService openUserService;

    /**
     * 查询认证申请记录表列表
     */
    @GetMapping("/list")
    @Node(value = "user::openAuthApplyRecords::list", name = "获取 认证申请记录表 列表")
    public R<PageSerializable<OpenAuthApplyRecords>> list(OpenAuthApplyRecords openAuthApplyRecords, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAuthApplyRecords> lqw = new LambdaQueryWrapper<OpenAuthApplyRecords>();
        lqw.eq(openAuthApplyRecords.getId() != null, OpenAuthApplyRecords::getId ,openAuthApplyRecords.getId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getApplyNo()),OpenAuthApplyRecords::getApplyNo ,openAuthApplyRecords.getApplyNo());
        lqw.eq(OpenAuthApplyRecords::getUserId, userDetails.getId());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAppId()),OpenAuthApplyRecords::getAppId ,openAuthApplyRecords.getAppId());
        lqw.eq(openAuthApplyRecords.getAuthType() != null, OpenAuthApplyRecords::getAuthType ,openAuthApplyRecords.getAuthType());
        lqw.eq(openAuthApplyRecords.getApplyStatus() != null, OpenAuthApplyRecords::getApplyStatus ,openAuthApplyRecords.getApplyStatus());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getFailReason()),OpenAuthApplyRecords::getFailReason ,openAuthApplyRecords.getFailReason());
        lqw.eq(openAuthApplyRecords.getAuditTime() != null, OpenAuthApplyRecords::getAuditTime ,openAuthApplyRecords.getAuditTime());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getAuditRemark()),OpenAuthApplyRecords::getAuditRemark ,openAuthApplyRecords.getAuditRemark());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getSourceIp()),OpenAuthApplyRecords::getSourceIp ,openAuthApplyRecords.getSourceIp());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getUserAgent()),OpenAuthApplyRecords::getUserAgent ,openAuthApplyRecords.getUserAgent());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getRequestData()),OpenAuthApplyRecords::getRequestData ,openAuthApplyRecords.getRequestData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getResponseData()),OpenAuthApplyRecords::getResponseData ,openAuthApplyRecords.getResponseData());
        lqw.like(StringUtils.isNotBlank(openAuthApplyRecords.getCallbackUrl()),OpenAuthApplyRecords::getCallbackUrl ,openAuthApplyRecords.getCallbackUrl());
        lqw.eq(openAuthApplyRecords.getCallbackStatus() != null, OpenAuthApplyRecords::getCallbackStatus ,openAuthApplyRecords.getCallbackStatus());
        lqw.eq(openAuthApplyRecords.getCallbackTime() != null, OpenAuthApplyRecords::getCallbackTime ,openAuthApplyRecords.getCallbackTime());
        lqw.eq(openAuthApplyRecords.getRetryCount() != null, OpenAuthApplyRecords::getRetryCount ,openAuthApplyRecords.getRetryCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAuthApplyRecords::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openAuthApplyRecords.getUpdatedTime() != null, OpenAuthApplyRecords::getUpdatedTime ,openAuthApplyRecords.getUpdatedTime());
        List<OpenAuthApplyRecords> list = openAuthApplyRecordsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取认证申请记录表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openAuthApplyRecords::getInfo", name = "获取 认证申请记录表 详细信息")
    public R<OpenAuthApplyRecords> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenAuthApplyRecords openAuthApplyRecords = openAuthApplyRecordsService.getById(id);
        if (!openAuthApplyRecords.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(openAuthApplyRecords);
    }

    /**
     * 获取用户审核记录
     */
    @GetMapping("/getUserAuditRecord")
    public R<OpenAuthApplyRecords> getUserAuditRecord(@AuthenticationPrincipal SohoUserDetails userDetails) {
        LambdaQueryWrapper<OpenAuthApplyRecords> openAuthApplyRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        openAuthApplyRecordsLambdaQueryWrapper.eq(OpenAuthApplyRecords::getUserId, userDetails.getId());
        OpenAuthApplyRecords openAuthApplyRecords = openAuthApplyRecordsService.getOne(openAuthApplyRecordsLambdaQueryWrapper, false);
        return R.success(openAuthApplyRecords);
    }

    /**
     * 新增认证申请记录表
     */
    @PostMapping
    @Node(value = "user::openAuthApplyRecords::add", name = "新增 认证申请记录表")
    public R<OpenAuthApplyRecords> add(@RequestBody AuthRequest authRequest, @AuthenticationPrincipal SohoUserDetails userDetails, HttpServletRequest request) {
        OpenUser openUser = openUserService.getOpenUserByUserId(userDetails.getId());
        Assert.notNull(openUser, "数据不存在");

        // 查找用户的认证记录
        LambdaQueryWrapper<OpenAuthApplyRecords> openAuthApplyRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        openAuthApplyRecordsLambdaQueryWrapper.eq(OpenAuthApplyRecords::getUserId, userDetails.getId());
        OpenAuthApplyRecords openAuthApplyRecords = openAuthApplyRecordsService.getOne(openAuthApplyRecordsLambdaQueryWrapper);
        if(openAuthApplyRecords == null) {
            openAuthApplyRecords = new OpenAuthApplyRecords();
            openAuthApplyRecords.setApplyNo(IDGeneratorUtils.snowflake().toString());
            openAuthApplyRecords.setRetryCount(0);
        } else {
            // 检查审核状态
            if(OpenAuthApplyRecordsEnums.ApplyStatus.APPROVED.getId() == (openAuthApplyRecords.getApplyStatus())) {
                return R.error("请勿重复提交");
            }
        }
        openAuthApplyRecords.setRetryCount(openAuthApplyRecords.getRetryCount() + 1);
        openAuthApplyRecords.setUserId(userDetails.getId());
        openAuthApplyRecords.setApplyStatus(OpenAuthApplyRecordsEnums.ApplyStatus.UNDER_REVIEW.getId());
        openAuthApplyRecords.setRequestData(JacksonUtils.toJson(authRequest));
        openAuthApplyRecords.setSourceIp(IpUtils.getClientIp());
        // 获取浏览器 user_agen 信息
        openAuthApplyRecords.setUserAgent(request.getHeader("User-Agent"));
        openAuthApplyRecordsService.saveOrUpdate(openAuthApplyRecords);
        return R.success(openAuthApplyRecords);
    }

    /**
     * 修改认证申请记录表
     */
    @PutMapping
    @Node(value = "user::openAuthApplyRecords::edit", name = "修改 认证申请记录表")
    public R<Boolean> edit(@RequestBody OpenAuthApplyRecords openAuthApplyRecords, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenAuthApplyRecords oldOpenAuthApplyRecords = openAuthApplyRecordsService.getById(openAuthApplyRecords.getId());
        Assert.notNull(oldOpenAuthApplyRecords, "数据不存在");
        if(!oldOpenAuthApplyRecords.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(openAuthApplyRecordsService.updateById(openAuthApplyRecords));
    }

    /**
     * 删除认证申请记录表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openAuthApplyRecords::remove", name = "删除 认证申请记录表")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<OpenAuthApplyRecords> oldList = openAuthApplyRecordsService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(OpenAuthApplyRecords item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(openAuthApplyRecordsService.removeByIds(Arrays.asList(ids)));
    }
}