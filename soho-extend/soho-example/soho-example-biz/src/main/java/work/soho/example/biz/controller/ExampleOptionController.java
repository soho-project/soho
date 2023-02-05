package work.soho.example.biz.controller;

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
import work.soho.example.biz.domain.ExampleOption;
import work.soho.example.biz.service.ExampleOptionService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 选项Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/exampleOption" )
public class ExampleOptionController {

    private final ExampleOptionService exampleOptionService;

    /**
     * 查询选项列表
     */
    @GetMapping("/list")
    @Node(value = "exampleOption::list", name = "选项;;option:id~value列表")
    public R<PageSerializable<ExampleOption>> list(ExampleOption exampleOption)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ExampleOption> lqw = new LambdaQueryWrapper<ExampleOption>();
        if (exampleOption.getId() != null){
            lqw.eq(ExampleOption::getId ,exampleOption.getId());
        }        if (exampleOption.getKey() != null){
            lqw.eq(ExampleOption::getKey ,exampleOption.getKey());
        }        if (exampleOption.getValue() != null){
            lqw.eq(ExampleOption::getValue ,exampleOption.getValue());
        }        if (exampleOption.getUpdatedTime() != null){
            lqw.eq(ExampleOption::getUpdatedTime ,exampleOption.getUpdatedTime());
        }        if (exampleOption.getCreatedTime() != null){
            lqw.eq(ExampleOption::getCreatedTime ,exampleOption.getCreatedTime());
        }        List<ExampleOption> list = exampleOptionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取选项详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "exampleOption::getInfo", name = "选项;;option:id~value详细信息")
    public R<ExampleOption> getInfo(@PathVariable("id" ) Long id) {
        return R.success(exampleOptionService.getById(id));
    }

    /**
     * 新增选项
     */
    @PostMapping
    @Node(value = "exampleOption::add", name = "选项;;option:id~value新增")
    public R<Boolean> add(@RequestBody ExampleOption exampleOption) {
        exampleOption.setCreatedTime(LocalDateTime.now());
        exampleOption.setUpdatedTime(LocalDateTime.now());
        return R.success(exampleOptionService.save(exampleOption));
    }

    /**
     * 修改选项
     */
    @PutMapping
    @Node(value = "exampleOption::edit", name = "选项;;option:id~value修改")
    public R<Boolean> edit(@RequestBody ExampleOption exampleOption) {
        exampleOption.setUpdatedTime(LocalDateTime.now());
        return R.success(exampleOptionService.updateById(exampleOption));
    }

    /**
     * 删除选项
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "exampleOption::remove", name = "选项;;option:id~value删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(exampleOptionService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该选项 options:id-value
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "exampleOption::options", name = "选项;;option:id~valueOptions")
    public R<HashMap<Integer, String>> options() {
        List<ExampleOption> list = exampleOptionService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(ExampleOption item: list) {
            map.put(item.getId(), item.getValue());
        }
        return R.success(map);
    }
}
