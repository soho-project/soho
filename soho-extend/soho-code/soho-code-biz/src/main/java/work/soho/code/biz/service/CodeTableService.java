package work.soho.code.biz.service;

import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author fang
* @description 针对表【code_table(代码表)】的数据库操作Service
* @createDate 2022-11-30 02:55:24
*/
public interface CodeTableService extends IService<CodeTable> {
    CodeTableVo getTableVoById(Integer id);

    /**
     * 获取表sql
     *
     * @param id
     * @return
     */
    String getSqlById(Integer id);

    /**
     * 数据库表转代码表
     *
     * @param remoteCodeTableVo
     */
    void table2CodeTable(CodeTableVo remoteCodeTableVo);
}
