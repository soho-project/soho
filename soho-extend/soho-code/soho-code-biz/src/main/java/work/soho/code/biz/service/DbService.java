package work.soho.code.biz.service;

import work.soho.code.api.vo.CodeTableVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface DbService {
    List<Object> getTableNames();

    CodeTableVo getTableByName(String name);

    void createTable(String sql);

    /**
     * 删除指定表
     *
     * @param tableName
     */
    void dropTable(String tableName);
}
