package work.soho.code.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.domain.CodeTable;
import work.soho.code.biz.domain.CodeTableColumn;
import work.soho.code.biz.mapper.CodeTableColumnMapper;
import work.soho.code.biz.mapper.CodeTableMapper;
import work.soho.code.biz.service.CodeTableService;
import work.soho.common.core.util.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<CodeTableColumn> columnList = codeTableColumnMapper.selectList(new LambdaQueryWrapper<CodeTableColumn>().eq(CodeTableColumn::getTableId, codeTable.getId()).orderByAsc(CodeTableColumn::getId));
        columnList.forEach(item->{
            CodeTableVo.Column column = BeanUtils.copy(item, CodeTableVo.Column.class);
            // 检查default value， 如果为 空字符串则设置为null
            if(column.getDefaultValue() != null && column.getDefaultValue().equals("")) {
                column.setDefaultValue(null);
            }
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
                    if(column.getLength() != null) {
                        columnStr += "("+column.getLength()+")";
                    }
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
            } else if (column.getIsNotNull() == null || column.getIsNotNull() == 0) {
                columnStr += " DEFAULT NULL";
            }
            if(column.getIsUnique() != null && column.getIsUnique() == 1) {
                columnStr += " unsigned";
            }
            if(column.getIsNotNull() != null && column.getIsNotNull() == 1) {
                columnStr += " NOT NULL";
            }
            if(column.getIsAutoIncrement() != null && column.getIsAutoIncrement() == 1) {
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
            if(column.getIsPk() != null && column.getIsPk() == 1) {
                sb.append("\n  PRIMARY KEY (`"+column.getName()+"`),");
            }
        }

        //去除最后一个逗号
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",")+1, "");
        //表其他属性
        sb.append("\n) ENGINE=InnoDB CHARSET=utf8 COMMENT='"+codeTableVo.getComment()+"';");
        return sb.toString();
    }

    /**
     * 同步数据库表到业务表
     *
     * @param remoteCodeTableVo
     */
    @Override
    public void table2CodeTable(CodeTableVo remoteCodeTableVo) {
        // 检查表是否存在
        CodeTable codeTable = getOne(new LambdaQueryWrapper<CodeTable>().eq(CodeTable::getName, remoteCodeTableVo.getName()));
        Assert.notNull(codeTable, "表不存在");

        codeTable.setTitle(remoteCodeTableVo.getTitle());
        codeTable.setComment(remoteCodeTableVo.getComment());
        updateById(codeTable);

        // 获取表中字段列表信息
        List<CodeTableColumn> columnList = codeTableColumnMapper.selectList(new LambdaQueryWrapper<CodeTableColumn>().eq(CodeTableColumn::getTableId, codeTable.getId()));
        Map<String, CodeTableColumn> columnMap = columnList.stream().collect(Collectors.toMap(CodeTableColumn::getName, item -> item));

        // 远程表字段map信息
        Map<String, CodeTableVo.Column> remoteColumnMap = remoteCodeTableVo.getColumnList().stream().collect(Collectors.toMap(CodeTableVo.Column::getName, item -> item));

        // 处理更新
        for(CodeTableVo.Column column: remoteCodeTableVo.getColumnList()) {
            CodeTableColumn codeTableColumn = columnMap.get(column.getName());
            if(codeTableColumn != null) {
                codeTableColumn.setScale(column.getScale());
                codeTableColumn.setLength(column.getLength());
                codeTableColumn.setComment(column.getComment());
                codeTableColumn.setDataType(column.getDataType());
                codeTableColumn.setIsAutoIncrement(column.getIsAutoIncrement());
                codeTableColumn.setIsNotNull(column.getIsNotNull());
                codeTableColumn.setIsPk(column.getIsPk());
                codeTableColumn.setIsUnique(column.getIsUnique());
                codeTableColumn.setDefaultValue(column.getDefaultValue());
                codeTableColumn.setIsZeroFill(column.getIsZeroFill());
                codeTableColumn.setTitle(column.getTitle());
                codeTableColumnMapper.updateById(codeTableColumn);
            }
        }

        // TODO 处理不存在的字段 进行删除操作
        for(CodeTableColumn codeTableColumn: columnList) {
            if(!remoteColumnMap.containsKey(codeTableColumn.getName())) {
                codeTableColumnMapper.deleteById(codeTableColumn.getId());
            }
        }
    }
}




