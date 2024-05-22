package work.soho.code.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class CodeTableVo {
    /**
     * ID
     */
    private Integer id;

    /**
     * 表名
     */
    private String name;

    /**
     * 表标题
     */
    private String title;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 字段列表
     */
    private List<Column> columnList = new ArrayList<>();

    /**
     * 表字段
     */
    @Data
    public static class Column {
        /**
         * ID
         */
        private Integer id;

        /**
         * 表ID;;frontType:option,foreign:code_table.id~title
         */
        private Integer tableId;

        /**
         * 表名
         */
        private String name;

        /**
         * 表标题
         */
        private String title;

        /**
         * 数据类型
         */
        private String dataType;

        /**
         * 是否主键;0:否,1:是;frontType:select
         */
        private Integer isPk = 0;

        /**
         * 是否不为空;0:否,1:是;frontType:select
         */
        private Integer isNotNull = 0;

        /**
         * 是否无符号;0:否,1:是;frontType:select
         */
        private Integer isUnique = 0;

        /**
         * 是否自增;0:否,1:是;frontType:select
         */
        private Integer isAutoIncrement = 0;

        /**
         * 是否0填充;0:否,1:是;frontType:select
         */
        private Integer isZeroFill = 0;

        /**
         * 默认值
         */
        private String defaultValue = null;

        /**
         * 长度
         */
        private Integer length;

        /**
         * 小数点位数
         */
        private Integer scale = 0;

        /**
         * 字段注释
         */
        private String comment;

        /**
         * 检查字段是否有差异
         *
         * @param obj
         * @return
         */
        public boolean equalsExcludingId(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            CodeTableVo.Column column = (CodeTableVo.Column) obj;
            System.out.println(this);
            System.out.println(column);
            System.out.println("===============================");
            return this.name.equals(column.name)
//                    && (this.title != null && this.title.equals(column.title))
                    && (this.dataType != null && this.dataType.equals(column.dataType))
                    && (this.isPk != null && this.isPk.equals(column.isPk) || (this.isPk == null && column.isPk == null))
                    && (this.isNotNull != null && this.isNotNull.equals(column.isNotNull) || (this.isNotNull == null && column.isNotNull == null))
                    && (this.isUnique != null && this.isUnique.equals(column.isUnique) || (this.isUnique == null && column.isUnique == null))
                    && (this.isAutoIncrement != null && this.isAutoIncrement.equals(column.isAutoIncrement) || (this.isAutoIncrement == null && column.isAutoIncrement == null))
                    && (this.isZeroFill != null && this.isZeroFill.equals(column.isZeroFill) || (this.isZeroFill == null && column.isZeroFill == null))
                    && (this.defaultValue != null && this.defaultValue.equals(column.defaultValue) || (this.defaultValue == null && column.defaultValue == null))
                    && (this.length != null && this.length.equals(column.length) || (this.length ==null && column.length == null))
                    && (this.scale != null && this.scale.equals(column.scale) || (this.scale == null && column.scale == null))
                    && (this.comment != null && this.comment.equals(column.comment) || (this.comment == null && column.comment == null));
        }

        //转换成sql定义
        public String toDefineSql() {
            String columnStr = "`"+this.getName()+"` " + this.getDataType();
            switch (this.getDataType()) {
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
                    if(this.getLength() != null) {
                        columnStr += "("+this.getLength()+")";
                    }
                    break;
                case "decimal": //带小数点
                case "float":
                case "double":
                    columnStr += "("+this.getLength()+"," + this.getScale()+ ")";
                    break;
                default:
                    //nothing todo1
                    break;
            }
            //默认值
            if(this.getDefaultValue() != null) {
                if(this.getDefaultValue().equals("NULL")) {
                    columnStr += " DEFAULT " + this.getDefaultValue();
                } else {
                    columnStr += " DEFAULT '" + this.getDefaultValue() + "'";
                }
            } else if (this.getIsNotNull() == null || this.getIsNotNull() == 0) {
                columnStr += " DEFAULT NULL";
            }
            if(this.getIsUnique() != null && this.getIsUnique() == 1) {
                columnStr += " unsigned";
            }
            if(this.getIsNotNull() != null && this.getIsNotNull() == 1) {
                columnStr += " NOT NULL";
            }
            if(this.getIsAutoIncrement() != null && this.getIsAutoIncrement() == 1) {
                columnStr += " AUTO_INCREMENT";
            }
            if(this.getComment() != null) {
                columnStr += " COMMENT '"+ this.getComment() +"'";
            }
            return columnStr;
        }
    }

    /**
     * 获取差异sql
     *
     * @param remoteCodeTableVo
     * @return
     */
    public String toDiffSql(CodeTableVo remoteCodeTableVo) {
        CodeTableVo codeTableVo =  this;

        List<CodeTableVo.Column> deleteList = new ArrayList<>();
        List<CodeTableVo.Column> addList = new ArrayList<>();
        List<CodeTableVo.Column> updateList = new ArrayList<>();
        //判断添加 更新
        codeTableVo.getColumnList().forEach(column -> {
            Optional<Column> remoteColumn = remoteCodeTableVo.getColumnList().stream().filter(item -> item.getName().equals(column.getName())).findFirst();
            if(remoteColumn.isPresent()) {
                //检查师傅需要更新
                if(!remoteColumn.get().equalsExcludingId(column)) {
                    updateList.add(column);
                }
            } else {
                addList.add(column);
            }
        });
        //查找删除字段
        remoteCodeTableVo.getColumnList().forEach(column-> {
            Optional<CodeTableVo.Column> remoteColumn = codeTableVo.getColumnList().stream().filter(item -> item.getName().equals(column.getName())).findFirst();
            if(remoteColumn.isEmpty()) {
                deleteList.add(column);
            }
        });

        StringBuilder sql = new StringBuilder("ALTER TABLE `" + codeTableVo.getName() + "` ");
        //修改
        for(CodeTableVo.Column column: updateList) {
            sql.append("\nCHANGE `").append(column.getName()).append("` ").append(column.toDefineSql()).append(",");
        }
        //新增字段
        for(CodeTableVo.Column column: addList) {
            sql.append("\nADD ").append(column.toDefineSql()).append(",");
        }
        //删除字段
        for(CodeTableVo.Column column: deleteList) {
            sql.append("\nDROP COLUMN `").append(column.getName()).append("`,");
        }
        //表备注修改
        sql.append("\n COMMENT '").append(codeTableVo.getComment()).append("',");
        //去除最后一行的逗号
        sql.deleteCharAt(sql.length()-1);
        sql.append(";");
        return sql.toString();
    }

    /**
     * 转创建表sql语句
     *
     * @return
     */
    public String toCreateSql() {
        StringBuilder sql = new StringBuilder("CREATE TABLE `"+this.name+"` (");
        for(CodeTableVo.Column column: this.columnList) {
            sql.append("\n ").append(column.toDefineSql()).append(",");
        }
        //查找主键
        Optional<Column> pkColumn = this.columnList.stream().filter(item-> item.isPk != null && item.isPk.equals(1)).findFirst();
        pkColumn.ifPresent(column -> sql.append("\n  PRIMARY KEY (`").append(column.getName()).append("`),"));
        //去除最后一行的逗号
        sql.deleteCharAt(sql.length()-1);
        sql.append("\n) ENGINE=InnoDB CHARSET=utf8 COMMENT='");
        sql.append(this.comment).append("';");
        return sql.toString();
    }
}
