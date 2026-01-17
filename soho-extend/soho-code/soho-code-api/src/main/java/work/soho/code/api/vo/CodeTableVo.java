package work.soho.code.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class CodeTableVo {
    /**
     * ID
     */
    private Integer id;

    /**
     * 数据源
     */
    private String dbSource;

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
                System.out.println("[equalsExcludingId] class not equal or null");
                return false;
            }

            CodeTableVo.Column other = (CodeTableVo.Column) obj;

            boolean equal = true;

            equal &= compare("name", this.name, other.name);
            equal &= compare("dataType", this.dataType, other.dataType);
            equal &= compare("isPk", this.isPk, other.isPk);
            equal &= compare("isNotNull", this.isNotNull, other.isNotNull);
            equal &= compare("isUnique", this.isUnique, other.isUnique);
            equal &= compare("isAutoIncrement", this.isAutoIncrement, other.isAutoIncrement);
            equal &= compare("isZeroFill", this.isZeroFill, other.isZeroFill);
            equal &= compareDefaultValue("defaultValue", this.defaultValue, other.defaultValue);
            equal &= compare("length", this.length, other.length);
            equal &= compare("scale", this.scale, other.scale);
            equal &= compare("comment", this.comment, other.comment);

            if (!equal) {
                System.out.println("[equalsExcludingId] NOT EQUAL");
                System.out.println("this   = " + this);
                System.out.println("other  = " + other);
            }

            return equal;
        }

        private static boolean compare(String field, Object a, Object b) {
            if (Objects.equals(a, b)) {
                return true;
            }
            System.out.println(
                    "[equalsExcludingId] field NOT equal -> " + field +
                            ", this=" + a +
                            ", other=" + b
            );
            return false;
        }

        private static boolean compareDefaultValue(String field, String a, String b) {
            if (Objects.equals(a, b)) {
                return true;
            }

            String aValue = (a == null ||"null".equalsIgnoreCase(a)) ? "NULL" : a;
            String bValue = (b == null || "null".equalsIgnoreCase(b)) ? "NULL" : b;
            if(aValue.equals(bValue)) {
                return true;
            }

            System.out.println(
                    "[equalsExcludingId] field NOT equal -> " + field +
                            ", this=" + a +
                            ", other=" + b
            );
            return false;
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
                //检查是否需要更新
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
        if(!codeTableVo.getComment().equals(remoteCodeTableVo.getComment())) {
            sql.append("\n COMMENT '").append(codeTableVo.getComment()).append("',");
        } else {
            // 如果没有任何修改返回空字符串
            if(updateList.isEmpty() && addList.isEmpty() && deleteList.isEmpty()) {
                return "";
            }
        }

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
