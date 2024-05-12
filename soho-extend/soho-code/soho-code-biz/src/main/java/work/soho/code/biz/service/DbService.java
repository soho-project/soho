package work.soho.code.biz.service;

import work.soho.code.api.vo.CodeTableVo;

import java.util.List;

public interface DbService {
    List<Object> getTableNames();

    CodeTableVo getTableByName(String name);

    void execute(String sql);

    /**
     * 删除指定表
     *
     * @param tableName
     */
    void dropTable(String tableName);

    /**
     * 检查表是否存在
     *
     * @param tableName
     * @return
     */
    Boolean isExistsTable(String tableName);
}
