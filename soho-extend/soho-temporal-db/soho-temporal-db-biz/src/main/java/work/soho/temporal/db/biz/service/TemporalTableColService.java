package work.soho.temporal.db.biz.service;

import org.apache.iotdb.isession.SessionDataSet;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TemporalTableColService extends IService<TemporalTableCol> {
    Boolean insertByCvs(Long colId, Long ts, String str);

    /**
     * 插入时序数据
     *
     * @param colId
     * @param time
     * @param str
     */
    void insertData(Long colId, Long time, String str);

    /**
     * 获取数据列表
     *
     * @param colList
     * @param where
     * @param limit
     * @param offset
     * @param order
     * @return
     */
    SessionDataSet getDataList(List<TemporalTableCol> colList, String where, Long limit, Long offset, String order);
}