package work.soho.temporal.db.biz.service.impl;

import org.apache.iotdb.common.rpc.thrift.TSStatus;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.service.rpc.thrift.TSInsertRecordReq;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.write.record.Tablet;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
public class LoTDBServiceImplTest {

    @Test
    public void testGetDbs() throws IoTDBConnectionException, StatementExecutionException {
        System.out.printf("test by fang");

        Session session = new Session.Builder().host("127.0.0.1")
                        .port(6667)
                        .username("root")
                        .password("root").build();
        session.open();

        String tsName = "root.ln.wf01.wt01.status";
        System.out.printf(String.valueOf(session));
        Boolean isExists = session.checkTimeseriesExists(tsName);
        System.out.printf("isExists = %s\n", isExists);

//        session.setStorageGroup("root.ln");
        String aTs = "root.ln.t1";
//        session.createTimeseries(aTs, TSDataType.INT64, TSEncoding.RLE, CompressionType.SNAPPY);

        List<String> measurements = new ArrayList<>();
        List<TSDataType> types = new ArrayList<>();
        measurements.add("s1");
        measurements.add("s2");
        measurements.add("s3");
        types.add(TSDataType.INT64);
        types.add(TSDataType.INT64);
        types.add(TSDataType.INT64);


        for (long time = 0; time < 100; time++) {
            List<Object> values = new ArrayList<>();
            values.add(1L);
            values.add(1L + 1);
            values.add(1L + 2);
            session.insertRecord(aTs, System.currentTimeMillis(), measurements, types, values);
        }
    }
}