package work.soho.temporal.db.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.StringUtils;
import work.soho.temporal.db.biz.config.TemporalDbConfig;
import work.soho.temporal.db.biz.domain.TemporalCategory;
import work.soho.temporal.db.biz.domain.TemporalTable;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import work.soho.temporal.db.biz.dto.Record;
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
    implements TemporalTableColService{
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
    private TSDataType getTsDataType(Integer type) {
        switch (type) {
            case 0:
                return TSDataType.BOOLEAN;
            case 1:
                return TSDataType.INT32;
            case 2:
                return TSDataType.INT64;
            case 3:
                return TSDataType.FLOAT;
            case 4:
                return TSDataType.DOUBLE;
            case 5:
                return TSDataType.TEXT;
            case 6:
                return TSDataType.VECTOR;
            case 7:
                return TSDataType.UNKNOWN;
        }
        return TSDataType.UNKNOWN;
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
        Assert.notNull(table);
        TemporalCategory category = temporalCategoryMapper.selectById(table.getCategoryId());
        Assert.notNull(category);
        return temporalDbConfig.getDefaultDbName() + "." + category.getName() + "." + table.getName();
    }
}