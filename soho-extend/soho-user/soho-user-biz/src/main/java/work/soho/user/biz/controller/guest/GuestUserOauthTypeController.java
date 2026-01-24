package work.soho.user.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.vo.UserOauthTypeVo;
import work.soho.user.biz.domain.UserOauthType;
import work.soho.user.biz.enums.UserOauthTypeEnums;
import work.soho.user.biz.service.UserOauthTypeService;

import java.util.List;
import java.util.stream.Collectors;

;

/**
 * 三方认证类型Controller
 *
 * @author fang
 */
@Api(value = "guest 三方认证类型", tags = "guest 三方认证类型")
@RequiredArgsConstructor
@RestController
@RequestMapping("user/guest/userOauthType" )
public class GuestUserOauthTypeController {

    private final UserOauthTypeService userOauthTypeService;

    /**
     * 查询三方认证类型列表
     */
    @GetMapping("/list")
    @Node(value = "guest::userOauthType::list", name = "获取 三方认证类型 列表")
    @ApiOperation(value = "guest 获取 三方认证类型 列表", notes = "guest 获取 三方认证类型 列表")
    public R<PageSerializable<UserOauthTypeVo>> list(UserOauthType userOauthType, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UserOauthType> lqw = new LambdaQueryWrapper<UserOauthType>();
        lqw.eq(userOauthType.getId() != null, UserOauthType::getId ,userOauthType.getId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getName()),UserOauthType::getName ,userOauthType.getName());
        lqw.like(StringUtils.isNotBlank(userOauthType.getTitle()),UserOauthType::getTitle ,userOauthType.getTitle());
        lqw.like(StringUtils.isNotBlank(userOauthType.getLogo()),UserOauthType::getLogo ,userOauthType.getLogo());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientId()),UserOauthType::getClientId ,userOauthType.getClientId());
        lqw.like(StringUtils.isNotBlank(userOauthType.getClientSecret()),UserOauthType::getClientSecret ,userOauthType.getClientSecret());
        lqw.eq(userOauthType.getStatus() != null, UserOauthType::getStatus ,userOauthType.getStatus());
        lqw.eq(userOauthType.getAdapter() != null, UserOauthType::getAdapter ,userOauthType.getAdapter());
        lqw.eq(userOauthType.getUpdatedTime() != null, UserOauthType::getUpdatedTime ,userOauthType.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserOauthType::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(UserOauthType::getStatus, UserOauthTypeEnums.Status.ACTIVE.getId());
        List<UserOauthType> list = userOauthTypeService.list(lqw);

        // 从 PageHelper 的结果中取 total（不要再手动 count）
        com.github.pagehelper.PageInfo<UserOauthType> pageInfo =
                new com.github.pagehelper.PageInfo<>(list);

        List<UserOauthTypeVo> listVo = list.stream()
                .map(i -> BeanUtils.copy(i, UserOauthTypeVo.class))
                .collect(Collectors.toList());

        PageSerializable<UserOauthTypeVo> pageSerializable = new PageSerializable<>(listVo);
        pageSerializable.setTotal(pageInfo.getTotal());

        return R.success(pageSerializable);
    }

    /**
     * 获取三方认证类型详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::userOauthType::getInfo", name = "获取 三方认证类型 详细信息")
    @ApiOperation(value = "guest 获取 三方认证类型 详细信息", notes = "guest 获取 三方认证类型 详细信息")
    public R<UserOauthType> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        UserOauthType userOauthType = userOauthTypeService.getById(id);
        return R.success(userOauthType);
    }

}