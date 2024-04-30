package work.soho.temporal.db.biz.service;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.SessionDataSet;
import work.soho.temporal.db.biz.dto.Record;
import work.soho.temporal.db.biz.iotdb.Query;

import java.util.List;

public interface IotdbService {
    void createdDb(String dbName);
    void insertRecord(String dbName, String tableName, long time, Record record) throws IoTDBConnectionException, StatementExecutionException;
    void insertRecords(String dbName, String tableName, long time, List<Record> records) throws IoTDBConnectionException, StatementExecutionException;
    SessionDataSet fetchAllRecords(String dbName, String tableName) throws IoTDBConnectionException, StatementExecutionException;
    SessionDataSet fetchAllRecords(String sql)  throws IoTDBConnectionException, StatementExecutionException;
    SessionDataSet fetchAllRecords(Query query) throws IoTDBConnectionException, StatementExecutionException;
}
