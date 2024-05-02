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
    void getDataList() throws IoTDBConnectionException, StatementExecutionException {
        List<TemporalTableCol> colList = temporalTableColService.listByIds(new ArrayList<>(List.of(1)));
        SessionDataSet dataSet = temporalTableColService.getDataList(colList, null,1000L, 0L, "Time desc");
        while (dataSet.hasNext()) {
            System.out.println(dataSet.next());
        }
    }
}