package work.soho.user.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BeanUtils;
import work.soho.user.api.dto.UserCertificationDto;
import work.soho.user.api.service.UserCertificationApiService;
import work.soho.user.biz.domain.UserCertification;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.enums.UserCertificationEnums;
import work.soho.user.biz.mapper.UserCertificationMapper;
import work.soho.user.biz.mapper.UserInfoMapper;
import work.soho.user.biz.service.UserCertificationService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserCertificationServiceImpl extends ServiceImpl<UserCertificationMapper, UserCertification>
    implements UserCertificationService, UserCertificationApiService {

    private final UserInfoMapper userInfoMapper;

    /**
     * 获取指定用户有效身份认证
     *
     * @param userId
     * @return
     */
    @Override
    public UserCertificationDto getActiveUserCertificationByUserId(Long userId) {
        LambdaQueryWrapper<UserCertification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCertification::getUserId, userId);
        queryWrapper.eq(UserCertification::getStatus, UserCertificationEnums.Status.CERTIFIED.getId());
        UserCertification userCertification = this.getOne(queryWrapper);
        if(userCertification == null) {
            return null;
        }
        return BeanUtils.copy(userCertification, UserCertificationDto.class);
    }

//    /**
//     * 获取指定用户的推荐用户数量
//     *
//     * @param userId
//     * @return
//     */
//    public Long getRecommendUserCount(Long userId) {
//        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(UserInfo::getPid, userId)
//                .eq(UserInfo::getStatus, 1);
//        List<Long> userIds = userInfoMapper.selectList(lqw).stream().map(UserInfo::getId).collect(Collectors.toList());
//
//        LambdaQueryWrapper<UserCertification> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(UserCertification::getUserId, userIds);
//        return this.count(queryWrapper);
//    }
}