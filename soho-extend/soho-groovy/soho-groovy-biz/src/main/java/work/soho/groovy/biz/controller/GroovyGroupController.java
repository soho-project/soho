package work.soho.groovy.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.api.admin.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.groovy.biz.domain.GroovyGroup;
import work.soho.groovy.biz.service.GroovyGroupService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * groovy分组Controller
 *
 * @author fang
 */
@Api(tags = "groovy分组")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/groovyGroup" )
public class GroovyGroupController {

    private final GroovyGroupService groovyGroupService;

    /**
     * 查询groovy分组列表
     */
    @GetMapping("/list")
    @Node(value = "groovyGroup::list", name = "groovy分组;;option:id~title列表")
    public R<PageSerializable<GroovyGroup>> list(GroovyGroup groovyGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<GroovyGroup> lqw = new LambdaQueryWrapper<GroovyGroup>();
        lqw.eq(groovyGroup.getId() != null, GroovyGroup::getId ,groovyGroup.getId());
        lqw.like(StringUtils.isNotBlank(groovyGroup.getName()),GroovyGroup::getName ,groovyGroup.getName());
        lqw.like(StringUtils.isNotBlank(groovyGroup.getTitle()),GroovyGroup::getTitle ,groovyGroup.getTitle());
        lqw.eq(groovyGroup.getStatus() != null, GroovyGroup::getStatus ,groovyGroup.getStatus());
        lqw.eq(groovyGroup.getUpdatedTime() != null, GroovyGroup::getUpdatedTime ,groovyGroup.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GroovyGroup::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GroovyGroup::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<GroovyGroup> list = groovyGroupService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取groovy分组详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "groovyGroup::getInfo", name = "groovy分组;;option:id~title详细信息")
    public R<GroovyGroup> getInfo(@PathVariable("id" ) Long id) {
        return R.success(groovyGroupService.getById(id));
    }

    /**
     * 新增groovy分组
     */
    @PostMapping
    @Node(value = "groovyGroup::add", name = "groovy分组;;option:id~title新增")
    public R<Boolean> add(@RequestBody GroovyGroup groovyGroup) {
        groovyGroup.setCreatedTime(LocalDateTime.now());
        groovyGroup.setUpdatedTime(LocalDateTime.now());
        return R.success(groovyGroupService.save(groovyGroup));
    }

    /**
     * 修改groovy分组
     */
    @PutMapping
    @Node(value = "groovyGroup::edit", name = "groovy分组;;option:id~title修改")
    public R<Boolean> edit(@RequestBody GroovyGroup groovyGroup) {
        groovyGroup.setUpdatedTime(LocalDateTime.now());
        return R.success(groovyGroupService.updateById(groovyGroup));
    }

    /**
     * 删除groovy分组
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "groovyGroup::remove", name = "groovy分组;;option:id~title删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(groovyGroupService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该groovy分组 options:id-title
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "groovyGroup::options", name = "groovy分组;;option:id~titleOptions")
    public R<HashMap<Integer, String>> options() {
        List<GroovyGroup> list = groovyGroupService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(GroovyGroup item: list) {
            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }
}
