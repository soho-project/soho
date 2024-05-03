package work.soho.temporal.db.biz.service.impl;

import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.temporal.db.api.dto.ColumnTimeOrder;
import work.soho.temporal.db.api.dto.Record;
import work.soho.temporal.db.api.dto.Row;
import work.soho.temporal.db.api.service.TemporalDbApi;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import work.soho.temporal.db.biz.service.TemporalTableColService;
import work.soho.test.TestApp;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class TemporalTableColServiceImplTest {
    @Autowired
    private TemporalTableColService temporalTableColService;

    @Autowired
    private TemporalDbApi temporalDbApi;

    @Test
    void insertByCvs() {
        String data = "1\n4234\n452345\n45345";
        temporalTableColService.insertByCvs(1L, System.currentTimeMillis(), data);
    }

    @Test
    void insertRecode() {
        for (int i = 0; i < 100; i++) {
            temporalTableColService.insertData(1L, System.currentTimeMillis(), "" + System.currentTimeMillis());
        }

    }

    @Test
    void insertRecodeTimeout() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            temporalTableColService.insertData(1L, System.currentTimeMillis(), "" + System.currentTimeMillis());
        }

    }

    @Test
    void getDataList() throws IoTDBConnectionException, StatementExecutionException {
        List<TemporalTableCol> colList = temporalTableColService.listByIds(new ArrayList<>(List.of(1)));
        SessionDataSet dataSet = temporalTableColService.getDataList(colList, null,1000L, 0L, "Time desc");
        while (dataSet.hasNext()) {
            System.out.println(dataSet.next());
        }
    }

    @Test
    void addRecord() {
        Record record = new Record();
        record.setName("c1");
        record.setValue(100L);
        record.setType(Record.DataType.INT64);
        temporalDbApi.addRecord(1, System.currentTimeMillis(), record);
    }

    @Test
    void addRecords() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            List<Record> list = new ArrayList<>();
            Thread.sleep(1002);
            Record record = new Record();
            record.setName("c1");
            record.setValue(System.currentTimeMillis());
            record.setType(Record.DataType.INT64);
            list.add(record);
            Record record2 = new Record();
            record2.setName("c2");
            record2.setValue(System.currentTimeMillis());
            record2.setType(Record.DataType.INT64);
            list.add(record2);
            temporalDbApi.addRecords(1, System.currentTimeMillis(), list);
        }
    }

    @Test
    void fetchRecodes() {
        List<Row> list = temporalDbApi.fetchColumn(1, 0L, System.currentTimeMillis(), 0, 100, ColumnTimeOrder.DESC);
        System.out.println(list);
    }
    @Test
    void fetchColumnsRecodes() {
        List<Integer> cols = new ArrayList<>();
        cols.add(1);
        cols.add(2);
        List<Row> list = temporalDbApi.fetchColumn(cols, 0L, System.currentTimeMillis(), 0, 100, ColumnTimeOrder.DESC);
        System.out.println(list);
    }
}