package work.soho.temporal.db.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.temporal.db.biz.domain.TemporalTable;
import work.soho.temporal.db.biz.service.TemporalTableService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * 时序表Controller
 *
 * @author fang
 */
@Api(tags = "时序表")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/temporalTable" )
public class TemporalTableController {

    private final TemporalTableService temporalTableService;

    /**
     * 查询时序表列表
     */
    @GetMapping("/list")
    @Node(value = "temporalTable::list", name = "时序表;;option:id~title列表")
    public R<PageSerializable<TemporalTable>> list(TemporalTable temporalTable, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<TemporalTable> lqw = new LambdaQueryWrapper<TemporalTable>();
        lqw.eq(temporalTable.getId() != null, TemporalTable::getId ,temporalTable.getId());
        lqw.like(StringUtils.isNotBlank(temporalTable.getName()),TemporalTable::getName ,temporalTable.getName());
        lqw.like(StringUtils.isNotBlank(temporalTable.getTitle()),TemporalTable::getTitle ,temporalTable.getTitle());
        lqw.eq(temporalTable.getUpdatedTime() != null, TemporalTable::getUpdatedTime ,temporalTable.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, TemporalTable::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, TemporalTable::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(temporalTable.getCategoryId() != null, TemporalTable::getCategoryId ,temporalTable.getCategoryId());
        List<TemporalTable> list = temporalTableService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取时序表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "temporalTable::getInfo", name = "时序表;;option:id~title详细信息")
    public R<TemporalTable> getInfo(@PathVariable("id" ) Long id) {
        return R.success(temporalTableService.getById(id));
    }

    /**
     * 新增时序表
     */
    @PostMapping
    @Node(value = "temporalTable::add", name = "时序表;;option:id~title新增")
    public R<Boolean> add(@RequestBody TemporalTable temporalTable) {
        return R.success(temporalTableService.save(temporalTable));
    }

    /**
     * 修改时序表
     */
    @PutMapping
    @Node(value = "temporalTable::edit", name = "时序表;;option:id~title修改")
    public R<Boolean> edit(@RequestBody TemporalTable temporalTable) {
        return R.success(temporalTableService.updateById(temporalTable));
    }

    /**
     * 删除时序表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "temporalTable::remove", name = "时序表;;option:id~title删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(temporalTableService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该时序表 options:id-title
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "temporalTable::options", name = "时序表;;option:id~titleOptions")
    public R<HashMap<Integer, String>> options() {
        List<TemporalTable> list = temporalTableService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(TemporalTable item: list) {
            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }
}