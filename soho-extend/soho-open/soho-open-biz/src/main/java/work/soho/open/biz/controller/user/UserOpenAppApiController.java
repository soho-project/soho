package work.soho.open.biz.controller.user;

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
import work.soho.common.security.annotation.Node;;
import work.soho.open.biz.domain.OpenAppApi;
import work.soho.open.biz.service.OpenAppApiService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/openAppApi" )
public class UserOpenAppApiController {

    private final OpenAppApiService openAppApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openAppApi::list", name = "获取  列表")
    public R<PageSerializable<OpenAppApi>> list(OpenAppApi openAppApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAppApi> lqw = new LambdaQueryWrapper<OpenAppApi>();
        lqw.eq(openAppApi.getId() != null, OpenAppApi::getId ,openAppApi.getId());
        lqw.eq(openAppApi.getAppId() != null, OpenAppApi::getAppId ,openAppApi.getAppId());
        lqw.eq(openAppApi.getApiId() != null, OpenAppApi::getApiId ,openAppApi.getApiId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenAppApi> list = openAppApiService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openAppApi::getInfo", name = "获取  详细信息")
    public R<OpenAppApi> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppApiService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openAppApi::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenAppApi openAppApi) {
        return R.success(openAppApiService.save(openAppApi));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openAppApi::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenAppApi openAppApi) {
        return R.success(openAppApiService.updateById(openAppApi));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openAppApi::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppApiService.removeByIds(Arrays.asList(ids)));
    }
}