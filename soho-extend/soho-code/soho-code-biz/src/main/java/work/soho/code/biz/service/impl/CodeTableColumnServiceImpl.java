package work.soho.code.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.service.CodeTableColumnService;
import work.soho.code.biz.mapper.CodeTableColumnMapper;
import org.springframework.stereotype.Service;

/**
* @author fang
* @description 针对表【code_table_column(代码表字段)】的数据库操作Service实现
* @createDate 2022-11-30 02:55:24
*/
@Service
public class CodeTableColumnServiceImpl extends ServiceImpl<CodeTableColumnMapper, CodeTableColumn>
    implements CodeTableColumnService{

}




