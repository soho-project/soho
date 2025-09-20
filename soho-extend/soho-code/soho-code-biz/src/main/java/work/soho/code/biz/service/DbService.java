package work.soho.code.biz.service;

import work.soho.code.api.vo.CodeTableVo;

import java.util.List;
import java.util.Set;

public interface DbService {
    List<Object> getTableNames();
    List<Object> getTableNames(String dbName);

    CodeTableVo getTableByName(String name);
    CodeTableVo getTableByName(String name, String dbName);

    void execute(String sql);
    void execute(String sql, String dbName);

    /**
     * 删除指定表
     *
     * @param tableName
     */
    void dropTable(String tableName);
    void dropTable(String tableName, String dbName);

    /**
     * 检查表是否存在
     *
     * @param tableName
     * @return
     */
    Boolean isExistsTable(String tableName);
    Boolean isExistsTable(String tableName, String dbName);

    Set<String> getDbList();
}
