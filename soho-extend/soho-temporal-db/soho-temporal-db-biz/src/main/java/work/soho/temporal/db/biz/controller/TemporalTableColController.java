package work.soho.temporal.db.biz.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import work.soho.temporal.db.biz.service.TemporalTableColService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * 时序表字段Controller
 *
 * @author Administrator
 */
@Api(tags = "时序数据库字段")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/temporalTableCol" )
public class TemporalTableColController {

    private final TemporalTableColService temporalTableColService;

    /**
     * 查询时序表字段列表
     */
    @GetMapping("/list")
    @Node(value = "temporalTableCol::list", name = "时序表字段列表")
    public R<PageSerializable<TemporalTableCol>> list(TemporalTableCol temporalTableCol, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<TemporalTableCol> lqw = new LambdaQueryWrapper<TemporalTableCol>();
        lqw.eq(temporalTableCol.getId() != null, TemporalTableCol::getId ,temporalTableCol.getId());
        lqw.like(StringUtils.isNotBlank(temporalTableCol.getName()),TemporalTableCol::getName ,temporalTableCol.getName());
        lqw.like(StringUtils.isNotBlank(temporalTableCol.getTitle()),TemporalTableCol::getTitle ,temporalTableCol.getTitle());
        lqw.eq(temporalTableCol.getTableId() != null, TemporalTableCol::getTableId ,temporalTableCol.getTableId());
        lqw.eq(temporalTableCol.getType() != null, TemporalTableCol::getType ,temporalTableCol.getType());
        lqw.eq(temporalTableCol.getUpdatedTime() != null, TemporalTableCol::getUpdatedTime ,temporalTableCol.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, TemporalTableCol::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, TemporalTableCol::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(temporalTableCol.getStatus() != null, TemporalTableCol::getStatus ,temporalTableCol.getStatus());
        List<TemporalTableCol> list = temporalTableColService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取时序表字段详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "temporalTableCol::getInfo", name = "时序表字段详细信息")
    public R<TemporalTableCol> getInfo(@PathVariable("id" ) Long id) {
        return R.success(temporalTableColService.getById(id));
    }

    /**
     * 新增时序表字段
     */
    @PostMapping
    @Node(value = "temporalTableCol::add", name = "时序表字段新增")
    public R<Boolean> add(@RequestBody TemporalTableCol temporalTableCol) {
        return R.success(temporalTableColService.save(temporalTableCol));
    }

    /**
     * 修改时序表字段
     */
    @PutMapping
    @Node(value = "temporalTableCol::edit", name = "时序表字段修改")
    public R<Boolean> edit(@RequestBody TemporalTableCol temporalTableCol) {
        return R.success(temporalTableColService.updateById(temporalTableCol));
    }

    /**
     * 删除时序表字段
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "temporalTableCol::remove", name = "时序表字段删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(temporalTableColService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取时序数据
     *
     * @param id
     * @return
     * @throws IoTDBConnectionException
     * @throws StatementExecutionException
     */
    @GetMapping("/getData/{id}")
    public R<List<HashMap<String, Object>>> getData(@PathVariable Long id) throws IoTDBConnectionException, StatementExecutionException {
        TemporalTableCol temporalTableCol = temporalTableColService.getById(id);
        Assert.notNull(temporalTableCol);
        List<TemporalTableCol> colList = new ArrayList<>();
        colList.add(temporalTableCol);

        SessionDataSet dataSet = temporalTableColService.getDataList(colList, null, 1000L, 0L, "Time desc");
        List<HashMap<String, Object>> list = new ArrayList<>();
        while (dataSet.hasNext()) {
            RowRecord rowRecord = dataSet.next();
            HashMap<String, Object> item = new HashMap<>();
            item.put("time", rowRecord.getTimestamp());
            Field field = rowRecord.getFields().get(0);
            item.put("val", field.getStringValue());
            list.add(item);
        }

        return R.success(list);
    }
}