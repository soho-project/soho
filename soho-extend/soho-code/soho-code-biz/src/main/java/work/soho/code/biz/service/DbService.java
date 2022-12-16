package work.soho.code.biz.service;

import work.soho.code.api.vo.CodeTableVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface DbService {
    public List<Object> getTableNames();

    public CodeTableVo getTableByName(String name);

    void createTable(String sql);
}
