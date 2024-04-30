package work.soho.temporal.db.biz.service.impl;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.SessionDataSet;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Service;
import work.soho.temporal.db.biz.iotdb.Query;
import work.soho.temporal.db.biz.service.IotdbService;
import work.soho.temporal.db.biz.dto.Record;

import java.util.ArrayList;
import java.util.List;

@Service
public class IoTdbServiceImpl implements IotdbService {

    @Override
    public void createdDb(String dbName) {

    }

    @Override
    public void insertRecord(String dbName, String tableName, long time, Record record) throws IoTDBConnectionException, StatementExecutionException {
        List<Record> list = new ArrayList<>();
                list.add(record);
        this.insertRecords(dbName, tableName, time, list);
    }

    /**
     * 插入记录
     *
     * @param dbName
     * @param tableName
     * @param records
     * @throws IoTDBConnectionException
     * @throws StatementExecutionException
     */
    @Override
    public void insertRecords(String dbName, String tableName, long time, List<Record> records) throws IoTDBConnectionException, StatementExecutionException {
        Session session = getSession();
        List<String> measurements = new ArrayList<>();
        List<TSDataType> types = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (Record record : records) {
            measurements.add(record.getName());
//            types.add(TSDataType.INT64);
            types.add(record.getType());
            values.add(record.getValue());
        }

        session.insertRecord(dbName + "." + tableName, time, measurements, types, values);
    }

    /**
     * 获取所有数据
     *
     * @param dbName
     * @param tableName
     * @return
     * @throws IoTDBConnectionException
     * @throws StatementExecutionException
     */
    public SessionDataSet fetchAllRecords(String dbName, String tableName) throws IoTDBConnectionException, StatementExecutionException {
        Session session = getSession();
        return session.executeQueryStatement("select * from " + dbName + "." + tableName + " limit 1");
    }

    public SessionDataSet fetchAllRecords(Query query) throws IoTDBConnectionException, StatementExecutionException {
        return fetchAllRecords(query.toString());
    }

    /**
     * 查询数据
     *
     * @param sql
     * @return
     * @throws StatementExecutionException
     * @throws IoTDBConnectionException
     */
    public SessionDataSet fetchAllRecords(String sql) throws StatementExecutionException, IoTDBConnectionException {
        Session session = getSession();
        return session.executeQueryStatement(sql);
    }

    /**
     * 获取iotdb Session
     *
     * @return
     */
    private Session getSession() throws IoTDBConnectionException {
        Session session = new Session.Builder().host("127.0.0.1")
                .port(6667)
                .username("root")
                .password("root").build();
        session.open();
        return session;
    }
}
