package work.soho.temporal.db.biz.iotdb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Query {
    private String databaseName;
    private String tableName;
    private List<String> columns = new ArrayList<String>();
    private long limit = 1000;
    private long offset = 0;
    private String where = "";
    private String orderBy = "";

    @Override
    public String toString() {
        String columnStr = "*";
        if(columns.size()>0) {
            columnStr = String.join(",", columns);
        }

        String whereStr = "";
        if(where != null && where.length()>0) {
            whereStr = " where " + where;
        }
        return "select "+columnStr+" from " + databaseName + "." + tableName + whereStr +" " +orderBy+ " limit " + limit + " offset " + offset;
    }
}
