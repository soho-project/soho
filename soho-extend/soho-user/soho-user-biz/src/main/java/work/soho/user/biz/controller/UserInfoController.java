package work.soho.user.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 用户信息;;option:id~usernameController
 *
 * @author fang
 * @date 2022-11-28 09:54:28
 */
@Api(tags = "用户信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/admin/userInfo" )
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 查询用户信息;;option:id~username列表
     */
    @GetMapping("/list")
    @Node(value = "userInfo::list", name = "用户信息;;option:id~username列表")
    public R<PageSerializable<UserInfo>> list(UserInfo userInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<UserInfo>();
        if (userInfo.getId() != null){
            lqw.eq(UserInfo::getId ,userInfo.getId());
        }
        if (StringUtils.isNotBlank(userInfo.getUsername())){
            lqw.like(UserInfo::getUsername ,userInfo.getUsername());
        }
        if (StringUtils.isNotBlank(userInfo.getEmail())){
            lqw.like(UserInfo::getEmail ,userInfo.getEmail());
        }
        if (StringUtils.isNotBlank(userInfo.getPhone())){
            lqw.like(UserInfo::getPhone ,userInfo.getPhone());
        }
        if (StringUtils.isNotBlank(userInfo.getPassword())){
            lqw.like(UserInfo::getPassword ,userInfo.getPassword());
        }
        if (StringUtils.isNotBlank(userInfo.getAvatar())){
            lqw.like(UserInfo::getAvatar ,userInfo.getAvatar());
        }
        if (userInfo.getStatus() != null){
            lqw.eq(UserInfo::getStatus ,userInfo.getStatus());
        }
        if (userInfo.getAge() != null){
            lqw.eq(UserInfo::getAge ,userInfo.getAge());
        }
        if (userInfo.getSex() != null){
            lqw.eq(UserInfo::getSex ,userInfo.getSex());
        }
        if (userInfo.getUpdatedTime() != null){
            lqw.eq(UserInfo::getUpdatedTime ,userInfo.getUpdatedTime());
        }
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UserInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        List<UserInfo> list = userInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户信息;;option:id~username详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "userInfo::getInfo", name = "用户信息;;option:id~username详细信息")
    public R<UserInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(userInfoService.getById(id));
    }

    /**
     * 新增用户信息;;option:id~username
     */
    @PostMapping
    @Node(value = "userInfo::add", name = "用户信息;;option:id~username新增")
    public R<Boolean> add(@RequestBody UserInfo userInfo) {
       userInfo.setUpdatedTime(LocalDateTime.now());
       userInfo.setCreatedTime(LocalDateTime.now());
        return R.success(userInfoService.save(userInfo));
    }

    /**
     * 修改用户信息;;option:id~username
     */
    @PutMapping
    @Node(value = "userInfo::edit", name = "用户信息;;option:id~username修改")
    public R<Boolean> edit(@RequestBody UserInfo userInfo) {
       userInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(userInfoService.updateById(userInfo));
    }

    /**
     * 删除用户信息;;option:id~username
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "userInfo::remove", name = "用户信息;;option:id~username删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(userInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options[name:id, capitalName:Id, capitalKeyName:Id, dbColName:id, type:long, frontType:number, frontName:Id, frontMax:99999999999, frontStep:, dbType:int, dbUnsigned:false, comment:ID, annos:, length:11, scale:0, frontLength:12, defaultValue:null, isNotNull:true, specification:int(11), dbForeignName:, capitalForeignName:, javaForeignName:, javaType:Integer]
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "userInfo::options", name = "用户信息;;option:id~usernameOptions")
    public R<HashMap<Integer, String>> options() {
        List<UserInfo> list = userInfoService.list();

        HashMap<Integer, String> map = new HashMap<>();
        for(UserInfo item: list) {
            map.put(item.getId(), item.getUsername());
        }
        return R.success(map);
    }
}
