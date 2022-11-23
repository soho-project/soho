package work.soho.example.biz.controller;

import java.time.LocalDateTime;

import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Arrays;
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
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.service.ExampleService;

/**
 * 自动化样例表Controller
 *
 * @author fang
 * @date 2022-11-15 23:37:18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/example" )
public class ExampleController {

    private final ExampleService exampleService;

    /**
     * 查询自动化样例表列表
     */
    @GetMapping("/list")
    @Node(value = "example::list", name = "自动化样例表列表")
    public R<PageSerializable<Example>> list(Example example, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<Example> lqw = new LambdaQueryWrapper<Example>();
        if (example.getId() != null){
            lqw.eq(Example::getId ,example.getId());
        }
        if (StringUtils.isNotBlank(example.getTitle())){
            lqw.like(Example::getTitle ,example.getTitle());
        }
        if (example.getCategoryId() != null){
            lqw.eq(Example::getCategoryId ,example.getCategoryId());
        }
        if (StringUtils.isNotBlank(example.getPayId())){
            lqw.like(Example::getPayId ,example.getPayId());
        }
        if (StringUtils.isNotBlank(example.getContent())){
            lqw.like(Example::getContent ,example.getContent());
        }
        if (example.getUpdatedTime() != null){
            lqw.eq(Example::getUpdatedTime ,example.getUpdatedTime());
        }
        if (example.getCreatedTime() != null){
            lqw.eq(Example::getCreatedTime ,example.getCreatedTime());
        }

        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, Example::getCreatedTime, betweenCreatedTimeRequest.getEndTime());

        List<Example> list = exampleService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取自动化样例表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "example::getInfo", name = "自动化样例表详细信息")
    public R<Example> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleService.getById(id));
    }

    /**
     * 新增自动化样例表
     */
    @PostMapping
    @Node(value = "example::add", name = "自动化样例表新增")
    public R<Boolean> add(@RequestBody Example example) {
       example.setUpdatedTime(LocalDateTime.now());
       example.setCreatedTime(LocalDateTime.now());
        return R.success(exampleService.save(example));
    }

    /**
     * 修改自动化样例表
     */
    @PutMapping
    @Node(value = "example::edit", name = "自动化样例表修改")
    public R<Boolean> edit(@RequestBody Example example) {
       example.setUpdatedTime(LocalDateTime.now());
        return R.success(exampleService.updateById(example));
    }

    /**
     * 删除自动化样例表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "example::remove", name = "自动化样例表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleService.removeByIds(Arrays.asList(ids)));
    }
}
