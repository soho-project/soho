package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.open.biz.domain.OpenAuthApplyRecords;
import work.soho.open.biz.domain.OpenUserEnterpriseAuth;
import work.soho.open.biz.domain.OpenUserPersonalAuth;
import work.soho.open.biz.enums.OpenAuthApplyRecordsEnums;
import work.soho.open.biz.mapper.OpenAuthApplyRecordsMapper;
import work.soho.open.biz.mapper.OpenUserEnterpriseAuthMapper;
import work.soho.open.biz.mapper.OpenUserPersonalAuthMapper;
import work.soho.open.biz.request.AuthRequest;
import work.soho.open.biz.service.OpenAuthApplyRecordsService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class OpenAuthApplyRecordsServiceImpl extends ServiceImpl<OpenAuthApplyRecordsMapper, OpenAuthApplyRecords>
    implements OpenAuthApplyRecordsService{

    private final OpenUserPersonalAuthMapper openUserPersonalAuthMapper;
    private final OpenUserEnterpriseAuthMapper openUserEnterpriseAuthMapper;

    @Override
    public Boolean audit(OpenAuthApplyRecords openAuthApplyRecords) {
        AuthRequest authRequest = JacksonUtils.toBean(openAuthApplyRecords.getRequestData(), AuthRequest.class);
        Assert.notNull(authRequest, "数据不存在");
        if(authRequest.getAuthType() == OpenAuthApplyRecordsEnums.AuthType.INDIVIDUAL.getId()) {
            // 个人认证
            // 删除历史记录
            LambdaQueryWrapper<OpenUserPersonalAuth> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OpenUserPersonalAuth::getUserId, openAuthApplyRecords.getUserId());
            openUserPersonalAuthMapper.delete(lambdaQueryWrapper);

            openAuthApplyRecords.setAuthType(OpenAuthApplyRecordsEnums.AuthType.INDIVIDUAL.getId());
            OpenUserPersonalAuth openUserPersonalAuth = BeanUtils.copy(authRequest.getPersonal(), OpenUserPersonalAuth.class);
            openUserPersonalAuth.setUserId(openAuthApplyRecords.getUserId());
            openUserPersonalAuthMapper.insert(openUserPersonalAuth);
        } else if(authRequest.getAuthType() == OpenAuthApplyRecordsEnums.AuthType.ENTERPRISE.getId()) {
            // 企业认证
            // 删除历史记录
            LambdaQueryWrapper<OpenUserEnterpriseAuth> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OpenUserEnterpriseAuth::getUserId, openAuthApplyRecords.getUserId());
            openUserEnterpriseAuthMapper.delete(lambdaQueryWrapper);

            openAuthApplyRecords.setAuthType(OpenAuthApplyRecordsEnums.AuthType.ENTERPRISE.getId());
            OpenUserEnterpriseAuth openUserEnterpriseAuth = BeanUtils.copy(authRequest.getEnterprise(), OpenUserEnterpriseAuth.class);
            openUserEnterpriseAuth.setUserId(openAuthApplyRecords.getUserId());
            openUserEnterpriseAuthMapper.insert(openUserEnterpriseAuth);
        }
        openAuthApplyRecords.setApplyStatus(OpenAuthApplyRecordsEnums.ApplyStatus.APPROVED.getId());
        openAuthApplyRecords.setFailReason(null);
        openAuthApplyRecords.setAuditTime(LocalDateTime.now());
        return updateById(openAuthApplyRecords);
    }

    @Override
    public Boolean reject(OpenAuthApplyRecords openAuthApplyRecords, String reason) {
        openAuthApplyRecords.setApplyStatus(OpenAuthApplyRecordsEnums.ApplyStatus.REJECTED_AFTER_REVIEW.getId());
//        openAuthApplyRecords.setAuditRemark( reason);
        openAuthApplyRecords.setAuditTime(LocalDateTime.now());
        openAuthApplyRecords.setFailReason(reason);
        return updateById(openAuthApplyRecords);
    }
}