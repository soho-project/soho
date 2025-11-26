package work.soho.code.biz.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import work.soho.code.api.vo.CodeTableVo;
import work.soho.code.biz.service.DbService;
import work.soho.common.core.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbServiceImpl implements DbService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final Environment env;

    private static final String DEFAULT_DB = "master";

    private static Pattern PK_PATTERN = Pattern.compile("PRIMARY KEY \\((.*)\\)"); // 主键
    private static Pattern COLUMN_TYPE_PATTERN = Pattern.compile("(.*)\\((\\d+)\\)"); // 字段类型，长度
    private static Pattern COLUMN_TYPE_POINT_LENGTH_PATTERN = Pattern.compile("(.*)\\((\\d+),(\\d+)\\)");
    @Override
    public List<Object> getTableNames() {
        return getTableNames(DEFAULT_DB);
    }

    @Override
    public List<Object> getTableNames(String dbName) {
        try {
            if(StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            List<Map<String, Object>> list = jdbcTemplate.queryForList("show tables;");
            return list.stream().map(item->item.values().stream().findFirst().get()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return new ArrayList<>();
    }


    @Override
    public CodeTableVo getTableByName(String name) {
        return getTableByName(name, DEFAULT_DB);
    }

    @Override
    public CodeTableVo getTableByName(String name, String dbName) {
        try {
            if(StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            List<Map<String, Object>> list = jdbcTemplate.queryForList("show create table `"+name+"`");
            String sql = "CREATE TABLE `pay_order` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `pay_id` int(11) NOT NULL COMMENT '支付方式ID;;frontType:select,foreign:pay_info.id~title',\n" +
                    "  `order_no` varchar(128) NOT NULL COMMENT '支付单号',\n" +
                    "  `tracking_no` varchar(128) NOT NULL COMMENT '外部跟踪单号',\n" +
                    "  `transaction_id` varchar(128) DEFAULT NULL COMMENT '支付供应商跟踪ID；例如微信，支付宝支付单号',\n" +
                    "  `amount` decimal(9,2) NOT NULL COMMENT '支付金额',\n" +
                    "  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select',\n" +
                    "  `payed_time` datetime DEFAULT NULL COMMENT '支付时间;;frontType:datetime',\n" +
                    "  `notify_url` varchar(500) DEFAULT NULL COMMENT '通知地址',\n" +
                    "  `created_time` datetime NOT NULL COMMENT '创建时间',\n" +
                    "  `updated_time` datetime NOT NULL COMMENT '更新时间',\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='支付单;option:id~title'";
            sql = (String)list.get(0).get("Create Table");

            String tableComment = "";
            String tableTitle = "";
            //正则获取表备注
            String tableCommentPatternStr = "\n\\).*'(.*)'";
            Pattern tableCommentPattern = Pattern.compile(tableCommentPatternStr);
            Matcher tableCommentMatch = tableCommentPattern.matcher(sql);
            if(tableCommentMatch.find()) {
                tableComment = tableCommentMatch.group(1);
            }
            if(!"".equals(tableComment)) {
                String[] parts = tableComment.split(";");
                if(parts.length>=1) {
                    tableTitle = parts[0];
                }
            }

            CodeTableVo codeTableVo = new CodeTableVo();
            codeTableVo.setDbSource(dbName);
            codeTableVo.setName(name);
            codeTableVo.setTitle(tableTitle);
            codeTableVo.setComment(tableComment);

            String pattern = "  (.*)[,\n]";
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(sql);
            while(matcher.find()) {
//            System.out.println(matcher.group(1));
                CodeTableVo.Column column = paseColumn(matcher.group(1));
                if(column != null) {
                    codeTableVo.getColumnList().add(column);
                }
            }

            //处理匹配主键信息
            matcher = PK_PATTERN.matcher(sql);
            while(matcher.find()) {
                String[] parts = matcher.group(1).replace("`", "").split(",");
                for (int i = 0; i < parts.length; i++) {
                    int finalI = i;
                    codeTableVo.getColumnList().stream().forEach(item -> {
                        if(item.getName().equals(parts[finalI])) {
                            item.setIsPk(1);
                        }
                    });
                }
            }

            return codeTableVo;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return null;
    }

    /**
     * 解析字段信息
     *
     * @param str
     * @return
     */
    private CodeTableVo.Column paseColumn(String str) {
        CodeTableVo.Column column = new CodeTableVo.Column();
        str = str.trim();
        //排除非字段识别
        if(!str.startsWith("`")) {
            return null;
        }

        int i = 2;
        String[] parts = str.split(" ");
        column.setName(parts[0].substring(1, parts[0].length() -1));
        column.setDataType(parts[1]);
        column.setIsPk(0);
        column.setIsUnique(0);
        column.setIsZeroFill(0);
        column.setIsAutoIncrement(0);

        while(i<parts.length) {
            if(parts[i].equals("unsigned")) {
                column.setIsUnique(1);
                i++;
                continue;
            }
            //检查是否填充
            if(parts[i].equals("zerofill")) {
                column.setIsZeroFill(1);
                i++;
                continue;
            }

            if(parts[i].equals("NOT")) {
                //NOT NULL
                i += 2;
                column.setIsNotNull(1);
                continue;
            }

            //处理自增涨
            if(parts[i].startsWith("AUTO_INCREMENT")) {
                //自增张
                column.setIsAutoIncrement(1);
                i++;
                continue;
            }

            //处理默认值
            if(parts[i].equals("DEFAULT")) {
                //获取默认值
                i++;
                if(parts[i].startsWith("'")) {
                    column.setDefaultValue(parts[i].substring(1, parts[i].length()-1));
                } else {
                    column.setDefaultValue(parts[i]);
                }
                i++;
                continue;
            }

            //检查注解
            if("COMMENT".equals(parts[i])) {
                i++;
                if(parts.length>i) {
                    // fixed 注释里面可能包含空格， 检查拼接完成注解内容
                    String commentStr = "";
                    while(i<parts.length && !parts[i].endsWith("'")) {
                        if(commentStr.length()>0) {
                            commentStr += " ";
                            commentStr += parts[i];
                        } else {
                            commentStr += parts[i];
                        }
                        i++;
                    }
                    column.setComment(commentStr.substring(1, commentStr.length()-2));
//                    column.setComment(parts[i].substring(1, parts[i].length()-2));
                    // 匹配标题；注释中第一个 ; 前面的内容即标题
                    if(column.getComment() != null) {
                        String commentParts[] = column.getComment().split(";");
                        if(commentParts.length>=1) {
                            column.setTitle(commentParts[0]);
                        }
                    }
                }
                i++;
                continue;
            }
//            System.out.println("居然没有匹配到： " +  parts[i]);
            i++;
        }

        //解析长度
        parseLength(column.getDataType(), column);
        return column;
    }

    /**
     * 解析字段类型
     *
     * @param str
     * @param column
     */
    private void parseLength(String str, CodeTableVo.Column column) {
        Matcher matcher = COLUMN_TYPE_PATTERN.matcher(str);
        if(matcher.find()) {
            column.setDataType(matcher.group(1));
            //解析小数点
            column.setLength(Integer.parseInt(matcher.group(2)));
        }

        //正则小数点位长度
        matcher = COLUMN_TYPE_POINT_LENGTH_PATTERN.matcher(str);
        if(matcher.find()) {
            column.setDataType(matcher.group(1));
            column.setLength(Integer.parseInt(matcher.group(2)));
            column.setScale(Integer.parseInt(matcher.group(3)));
        }
    }

    /**
     * 创建sql
     *
     * @param sql
     */
    public void execute(String sql) {
        execute(sql, DEFAULT_DB);
    }

    @Override
    public void execute(String sql, String dbName) {
        try {
            if(StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 删除指定表
     *
     * @param tableName
     */
    public void dropTable(String tableName) {
        dropTable(tableName, DEFAULT_DB);
    }

    @Override
    public void dropTable(String tableName, String dbName) {
        try {
            if(StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            jdbcTemplate.execute("drop table if exists `" + tableName + "`;");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    @Override
    public Boolean isExistsTable(String tableName) {
        return isExistsTable(tableName, DEFAULT_DB);
    }

    @Override
    public Boolean isExistsTable(String tableName, String dbName) {
        boolean isExistsTable = false;
        try {
            if(StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            String sql = "SHOW TABLES LIKE '"+tableName+"';";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            isExistsTable =  list.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return isExistsTable;
    }

    @Override
    public Set<String> getDbList() {
        // 属性前缀
        String prefix = "spring.datasource.dynamic.datasource.";
        try {
            // 获取Environment中所有以prefix开头的属性名
            // 注意：此方法依赖于Spring Boot的具体实现和环境，在某些环境下可能无法获取到所有属性名
            String[] propertyNames = ((ConfigurableEnvironment) env).getPropertySources()
                    .stream()
                    .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
                    .map(propertySource -> ((EnumerablePropertySource<?>) propertySource).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .filter(propertyName -> propertyName.startsWith(prefix))
                    .toArray(String[]::new);

            if (propertyNames.length == 0) {
                return Collections.emptySet();
            }

            // 提取子数据源的key
            // 例如：将 "spring.datasource.dynamic.datasource.master.url" 中的 "master" 提取出来
            return Arrays.stream(propertyNames)
                    .map(propertyName -> propertyName.substring(prefix.length())) // 去掉前缀
                    .map(subProperty -> {
                        int firstDotIndex = subProperty.indexOf('.');
                        return (firstDotIndex != -1) ? subProperty.substring(0, firstDotIndex) : subProperty;
                    }) // 取第一个点号之前的部分
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            // 处理异常或记录日志
            return Collections.emptySet();
        }
    }
}
