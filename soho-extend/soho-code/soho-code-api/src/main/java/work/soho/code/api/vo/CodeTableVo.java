package work.soho.code.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    }
}
