package work.soho.temporal.db.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.StringUtils;
import work.soho.temporal.db.api.dto.*;
import work.soho.temporal.db.api.service.TemporalDbApi;
import work.soho.temporal.db.biz.config.TemporalDbConfig;
import work.soho.temporal.db.biz.domain.TemporalCategory;
import work.soho.temporal.db.biz.domain.TemporalTable;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import work.soho.temporal.db.biz.iotdb.Query;
import work.soho.temporal.db.biz.mapper.TemporalCategoryMapper;
import work.soho.temporal.db.biz.mapper.TemporalTableColMapper;
import work.soho.temporal.db.biz.mapper.TemporalTableMapper;
import work.soho.temporal.db.biz.service.IotdbService;
import work.soho.temporal.db.biz.service.TemporalTableColService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TemporalTableColServiceImpl extends ServiceImpl<TemporalTableColMapper, TemporalTableCol>
    implements TemporalTableColService, TemporalDbApi {
    private final TemporalTableMapper temporalTableMapper;
    private final TemporalCategoryMapper temporalCategoryMapper;
    private final IotdbService iotdbService;
    private final TemporalDbConfig temporalDbConfig;

    @Override
    public Boolean insertByCvs(Long colId, Long ts, String str) {
        if(StringUtils.isEmpty(str)) {
            return false;
        }

        TemporalTableCol col = getById(colId);
        Assert.notNull(col);
        TemporalTable table = temporalTableMapper.selectById(col.getTableId());
        Assert.notNull(table);
        TemporalCategory category = temporalCategoryMapper.selectById(table.getCategoryId());
        Assert.notNull(category);

        String dbName = "root." + category.getName();

        //切分换行
        ArrayList<Record> list = new ArrayList<Record>();
        String[] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(",");
            Record record = new Record();
            if(parts.length>1) {
                record.setValue(getValByType(parts[1], col.getType()));
            } else {
                record.setValue(getValByType(parts[0], col.getType()));
            }
            record.setName(col.getName());
            record.setType(getTsDataType(col.getType()));
            try {
                iotdbService.insertRecord(dbName, table.getName(), System.currentTimeMillis(), record);
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            }
        }

        return null;
    }

    public void insertData(Long colId, Long time, String str) {
        if(StringUtils.isEmpty(str)) {
            return;
        }

        TemporalTableCol col = getById(colId);
        Assert.notNull(col);
        TemporalTable table = temporalTableMapper.selectById(col.getTableId());
        Assert.notNull(table);
        TemporalCategory category = temporalCategoryMapper.selectById(table.getCategoryId());
        Assert.notNull(category);

        String dbName = temporalDbConfig.getDefaultDbName() + "." + category.getName();
        Record record = new Record();
        record.setValue(getValByType(str, col.getType()));
        record.setName(col.getName());
        record.setType(getTsDataType(col.getType()));
        try {
            iotdbService.insertRecord(dbName, table.getName(), System.currentTimeMillis(), record);
        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }
    }

    @Override
    public SessionDataSet getDataList(List<TemporalTableCol> colList, String where, Long limit, Long offset, String order) {
        try {
            List<String> colNames = colList.stream().map(TemporalTableCol::getName).collect(Collectors.toList());
            String tableName = getTableName(colList.get(0));
            if(order == null) {
                order = " Time Desc";
            }
            if(limit == null) {
                limit =  10000L;
            }
            if(offset == null) {
                offset = 0L;
            }

            if(StringUtils.isNotEmpty(where)) {
                where = " where " + where;
            } else {
                where = "";
            }

            String sql = "select " + String.join(",", colNames) + " from " + tableName + where +" order by "+ order + " offset "+ offset + " limit " + limit;
            System.out.println(sql);
            return iotdbService.fetchAllRecords(sql);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 转化值
     *
     * @param val
     * @param type
     * @return
     */
    private Object getValByType(String val, Integer type) {
        switch (type) {
            case 0:
                if(val == "1" || val == "true" || val == "TRUE") {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            case 1:
            case 2:
                return Long.valueOf(val);
            case 3:
                return Float.valueOf(val);
            case 4:
                return Double.valueOf(val);
            case 5:
                return val;
            case 6:
            case 7:
                return val;
        }
        return val;
    }

    /**
     * 获取数据类型
     *
     * @param type
     * @return
     */
    private Record.DataType getTsDataType(Integer type) {
        switch (type) {
            case 0:
                return Record.DataType.BOOLEAN;
            case 1:
                return Record.DataType.INT32;
            case 2:
                return Record.DataType.INT64;
            case 3:
                return Record.DataType.FLOAT;
            case 4:
                return Record.DataType.DOUBLE;
            case 5:
                return Record.DataType.TEXT;
            case 6:
                return Record.DataType.VECTOR;
            case 7:
                return Record.DataType.UNKNOWN;
        }
        return Record.DataType.UNKNOWN;
    }

    /**
     * 获取字段完整路径名
     *
     * @param col
     * @return
     */
    private String getFullName(TemporalTableCol col) {
        return getTableName(col) + "." + col.getName();
    }

    /**
     * 获取字段表名
     *
     * @param col
     * @return
     */
    private String getTableName(TemporalTableCol col) {
        TemporalTable table = temporalTableMapper.selectById(col.getTableId());
        return getTableName(table) + "." + table.getName();
    }

    /**
     * 获取表名
     *
     * @param temporalTable
     * @return
     */
    private String getTableName(TemporalTable temporalTable) {
        Assert.notNull(temporalTable);
        TemporalCategory category = temporalCategoryMapper.selectById(temporalTable.getCategoryId());
        Assert.notNull(category);
        //TODO 递归分类名
        return category.getName() + "." + temporalTable.getName();
    }

    @Override
    public void addRecord(Integer tableId, Long time, Record record) {
        TemporalTable table = temporalTableMapper.selectById(tableId);
        try {
            System.out.println("............");
            System.out.println(time);
            System.out.println(record);
            iotdbService.insertRecord(temporalDbConfig.getDefaultDbName(), getTableName(table), time, record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRecords(Integer tableId, Long time, List<Record> list) {
        for(Record record: list) {
            addRecord(tableId, time, record);
        }
    }

    /**
     * 获取指定时间线数据
     *
     * @param colId
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @param columnTimeOrder
     * @return
     */
    @Override
    public List<Row> fetchColumn(Integer colId, Long startTime, Long endTime, Integer offset, Integer limit, ColumnTimeOrder columnTimeOrder) {
        List<Integer> list = new ArrayList<>();
        list.add(colId);
        return fetchColumn(list, startTime, endTime, offset, limit, columnTimeOrder);
    }

    @Override
    public List<Row> fetchColumn(List<Integer> colIds, Long startTime, Long endTime, Integer offset, Integer limit, ColumnTimeOrder columnTimeOrder) {
        List<TemporalTableCol> cols = getBaseMapper().selectBatchIds(colIds);
        //检查是否为同一个表
        Integer tableId = null;
        for(TemporalTableCol col: cols) {
            if(tableId == null) {
                tableId = col.getTableId();
            } else if(!tableId.equals(col.getTableId())) {
                throw new RuntimeException("字段不在同一个表， 情检查配置");
            }
        }
        //检查字段是否都存在
        if(colIds.size() != cols.size()) {
            throw new RuntimeException("请确定字段参数是否正确");
        }

        List<Row> list = new ArrayList<>();
        Query query = new Query();
        List<String> columns = new ArrayList<>();
        cols.forEach(item->{
            columns.add(item.getName());
        });

        TemporalTable table = temporalTableMapper.selectById(tableId);
        query.setColumns(columns);
        query.setLimit(limit);
        query.setOffset(offset);
        query.setOrderBy("Time " + columnTimeOrder.getOrder());
        query.setTableName(getTableName(table));
        query.setDatabaseName(temporalDbConfig.getDefaultDbName());
        query.setOrderBy("order by Time " + ColumnTimeOrder.DESC.getOrder());
        //设置限制条件
        String where = "";
        if(startTime != null) {
            where += "Time>=" + startTime;
        }
        if(endTime != null) {
            if(where != "") {
                where += " and ";
            }
            where += "Time<=" + endTime;
        }
        if(!StringUtils.isNotEmpty(where)) {
            query.setWhere(where);
        }

        try {
            SessionDataSet sessionDataSet = iotdbService.fetchAllRecords(query);
            while (sessionDataSet.hasNext()) {
                RowRecord rowRecord = sessionDataSet.next();
                Field field = rowRecord.getFields().get(0);
                Row row = new Row();
                row.setTimestamp(rowRecord.getTimestamp());
                rowRecord.getFields().forEach(item->{
                    Row.Column column = new Row.Column();
                    System.out.println(item);
                    if(item == null || item.getDataType() == null) {
                        row.getColumns().add(null);
                    } else {
                        System.out.println(item);
                        System.out.println(item.getDataType());
                        switch (item.getDataType()) {
                            case INT64:
                                column.setLongV(item.getLongV());
                                column.setDataType(Row.DataType.INT64);
                                break;
                            case INT32:
                                column.setIntV(item.getIntV());
                                column.setDataType(Row.DataType.INT32);
                                break;
                            case FLOAT:
                                column.setFloatV(item.getFloatV());
                                column.setDataType(Row.DataType.FLOAT);
                                break;
                            case TEXT:
                                Binary binary = new Binary(item.getBinaryV().getValues());
                                column.setDataType(Row.DataType.TEXT);
                                column.setBinaryV(binary);
                                break;
                            case BOOLEAN:
                                column.setBoolV(item.getBoolV());
                                column.setDataType(Row.DataType.BOOLEAN);
                                break;
                            case DOUBLE:
                                column.setDoubleV(item.getDoubleV());
                                column.setDataType(Row.DataType.DOUBLE);
                                break;
                            default:
                                break;
                        }
                        row.getColumns().add(column);
                    }
                });
                list.add(row);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}