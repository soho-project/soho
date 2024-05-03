package work.soho.temporal.db.api.service;

import work.soho.temporal.db.api.dto.ColumnTimeOrder;
import work.soho.temporal.db.api.dto.Record;
import work.soho.temporal.db.api.dto.Row;

import java.util.List;

public interface TemporalDbApi {
    /**
     * 指定分类添加记录
     */
    void addRecord(Integer tableId, Long time, Record record);

    void addRecords(Integer tableId, Long time, List<Record> list);

    List<Row> fetchColumn(Integer colId, Long startTime, Long endTime, Integer offset, Integer limit, ColumnTimeOrder columnTimeOrder);

    List<Row> fetchColumn(List<Integer> colIds, Long startTime, Long endTime, Integer offset, Integer limit, ColumnTimeOrder columnTimeOrder);
}
