package work.soho.code.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.mapper.CodeTableColumnMapper;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.mapper.CodeTableMapper;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BeanUtils;

import java.util.List;

/**
* @author fang
* @description 针对表【code_table(代码表)】的数据库操作Service实现
* @createDate 2022-11-30 02:55:24
*/
@RequiredArgsConstructor
@Service
public class CodeTableServiceImpl extends ServiceImpl<CodeTableMapper, CodeTable>
    implements CodeTableService{
    private final CodeTableColumnMapper codeTableColumnMapper;

    @Override
    public CodeTableVo getTableVoById(Integer id) {
        CodeTable codeTable = getById(id);
        if(codeTable == null) {
            return null;
        }
        CodeTableVo codeTableVo = BeanUtils.copy(codeTable, CodeTableVo.class);
        //获取表字段信息
        List<CodeTableColumn> columnList = codeTableColumnMapper.selectList(new LambdaQueryWrapper<CodeTableColumn>().eq(CodeTableColumn::getTableId, codeTable.getId()));
        columnList.forEach(item->{
            CodeTableVo.Column column = BeanUtils.copy(item, CodeTableVo.Column.class);
            codeTableVo.getColumnList().add(column);
        });
        return codeTableVo;
    }

    /**
     * 获取表sql
     *
     * TODO enum, set 类型支持
     *
     * @link  https://dev.mysql.com/doc/refman/5.7/en/data-types.html
     * @param id
     * @return
     */
    @Override
    public String getSqlById(Integer id) {
        CodeTableVo codeTableVo = getTableVoById(id);
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE `"+codeTableVo.getName()+"` (");
        //生成字段信息
        for (CodeTableVo.Column column: codeTableVo.getColumnList()) {
            String columnStr = "  `"+column.getName()+"` " + column.getDataType();
            switch (column.getDataType()) {
                    //数字
                case "bit":
                case "tinyint":
                case "smallint":
                case "mediumint":
                case "int":
                case "integer":
                case "bigint":
                    //字符串
                case "varchar":
                case "char":
                case "blob":
                case "text":
                    columnStr += "("+column.getLength()+")";
                    break;
                case "decimal": //带小数点
                case "float":
                case "double":
                    columnStr += "("+column.getLength()+"," + column.getScale()+ ")";
                    break;
                default:
                    //nothing todo1
                    break;
            }
            //默认值
            if(column.getDefaultValue() != null) {
                if(column.getDefaultValue().equals("NULL")) {
                    columnStr += " DEFAULT " + column.getDefaultValue();
                } else {
                    columnStr += " DEFAULT '" + column.getDefaultValue() + "'";
                }
            } else if (column.getIsNotNull() == 0) {
                columnStr += " DEFAULT NULL";
            }
            if(column.getIsUnique() == 1) {
                columnStr += " unsigned";
            }
            if(column.getIsNotNull() == 1) {
                columnStr += " NOT NULL";
            }
            if(column.getIsAutoIncrement() == 1) {
                columnStr += " AUTO_INCREMENT";
            }
            if(column.getComment() != null) {
                columnStr += " COMMENT '"+ column.getComment() +"'";
            }
            columnStr += ",";
            sb.append("\n");
            sb.append(columnStr);
        }

        //索引
        for(CodeTableVo.Column column: codeTableVo.getColumnList()) {
            if(column.getIsPk() == 1) {
                sb.append("\n  PRIMARY KEY (`"+column.getName()+"`),");
            }
        }

        //去除最后一个逗号
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",")+1, "");
        //表其他属性
        sb.append("\n) ENGINE=InnoDB CHARSET=utf8 COMMENT='"+codeTableVo.getComment()+"'");
        return sb.toString();
    }
}




