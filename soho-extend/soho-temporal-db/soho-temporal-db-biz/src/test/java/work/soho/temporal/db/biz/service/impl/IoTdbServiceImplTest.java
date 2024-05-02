package work.soho.temporal.db.biz.service.impl;

import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.temporal.db.biz.dto.Record;
import work.soho.temporal.db.biz.service.IotdbService;
import work.soho.test.TestApp;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class IoTdbServiceImplTest {
    @Autowired
    private IotdbService iotdbService;

    @Test
    void createdDb() {
    }

    @Test
    void insertRecord() throws IoTDBConnectionException, StatementExecutionException {
        for (int i = 0; i < 100; i++) {
            Record record = new Record();
            record.setType(TSDataType.INT64);
            record.setValue(System.currentTimeMillis());
            record.setName("col1");
            iotdbService.insertRecord("root.ln", "demo1", System.currentTimeMillis(), record);
        }

    }

    @Test
    void insertRecords() {
    }

    @Test
    void fetchAllRecords() throws IoTDBConnectionException, StatementExecutionException {
        SessionDataSet sessionDataSet = iotdbService.fetchAllRecords("root.ln", "demo1");
        System.out.println(sessionDataSet);
        while (sessionDataSet.hasNext()) {
            System.out.println(sessionDataSet.next());
        }
    }


    @Test
    void testFetchAllRecords() {
    }

//    @Test
//    void testFetchAllRecords1() throws IoTDBConnectionException, StatementExecutionException {
//        Query query = new Query();
//        query.setDatabaseName("root.ln");
//        query.setTableName("demo1");
//        query.setLimit(2);
//
//        SessionDataSet sessionDataSet = iotdbService.fetchAllRecords(query);
//        System.out.println(sessionDataSet);
//        while (sessionDataSet.hasNext()) {
//            System.out.println(sessionDataSet.next());
//        }
//
//    }
}