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

    // 表级 COMMENT='xxx'
    private static final Pattern TABLE_COMMENT_PATTERN = Pattern.compile("\\bCOMMENT\\s*=\\s*'((?:\\\\'|[^'])*)'");

    // 列定义行：`col` varchar(128) NOT NULL DEFAULT 'x' COMMENT '...'
    private static final Pattern COLUMN_LINE_PATTERN =
            Pattern.compile("^\\s*`([^`]+)`\\s+([^\\s]+)\\s*(.*)$");

    // COMMENT '...'
    private static final Pattern COLUMN_COMMENT_PATTERN =
            Pattern.compile("\\bCOMMENT\\b\\s+'((?:\\\\'|[^'])*)'");

    // DEFAULT xxx / DEFAULT 'xxx' / DEFAULT CURRENT_TIMESTAMP(3)
    private static final Pattern COLUMN_DEFAULT_PATTERN =
            Pattern.compile("\\bDEFAULT\\b\\s+((?:'(?:\\\\'|[^'])*')|(?:\\([^)]*\\))|(?:[^\\s,]+))");

    // PRIMARY KEY (`a`,`b`)
    private static final Pattern PRIMARY_KEY_PATTERN =
            Pattern.compile("\\bPRIMARY\\s+KEY\\b\\s*\\(([^)]*)\\)");

    // UNIQUE KEY xxx (`a`,`b`) / UNIQUE INDEX xxx (`a`)
    private static final Pattern UNIQUE_KEY_PATTERN =
            Pattern.compile("\\bUNIQUE\\s+(?:KEY|INDEX)\\b\\s+`?[^`(\\s]+`?\\s*\\(([^)]*)\\)");

    // 普通索引 KEY xxx (`a`) —— 这里暂不标记到 Column，仅保留扩展点
    // private static final Pattern KEY_PATTERN =
    //         Pattern.compile("\\bKEY\\b\\s+`?[^`(\\s]+`?\\s*\\(([^)]*)\\)");

    // 解析类型：int(11) / decimal(9,2) / varchar(128)
    private static final Pattern TYPE_NUMERIC_LEN_SCALE_PATTERN =
            Pattern.compile("^([a-zA-Z]+)(?:\\((\\d+)(?:,(\\d+))?\\))?.*$");

    @Override
    public List<Object> getTableNames() {
        return getTableNames(DEFAULT_DB);
    }

    @Override
    public List<Object> getTableNames(String dbName) {
        try {
            if (StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            List<Map<String, Object>> list = jdbcTemplate.queryForList("show tables;");
            return list.stream()
                    .map(item -> item.values().stream().findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
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
            if (StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);

            List<Map<String, Object>> list = jdbcTemplate.queryForList("show create table `" + name + "`");
            if (list == null || list.isEmpty()) {
                return null;
            }
            Object createObj = list.get(0).get("Create Table");
            if (!(createObj instanceof String) || StringUtils.isEmpty((String) createObj)) {
                return null;
            }
            String sql = (String) createObj;

            // 1) 表注释/标题
            String tableComment = extractTableComment(sql);
            String tableTitle = "";
            if (!StringUtils.isEmpty(tableComment)) {
                String[] parts = tableComment.split(";");
                if (parts.length >= 1) {
                    tableTitle = parts[0];
                }
            }

            CodeTableVo codeTableVo = new CodeTableVo();
            codeTableVo.setDbSource(dbName);
            codeTableVo.setName(name);
            codeTableVo.setTitle(tableTitle);
            codeTableVo.setComment(tableComment);

            // 2) 先按行处理：字段、主键、唯一键
            String[] lines = sql.split("\n");

            // 2.1 解析字段行
            for (String line : lines) {
                CodeTableVo.Column col = parseColumnLine(line);
                if (col != null) {
                    codeTableVo.getColumnList().add(col);
                }
            }

            // 2.2 解析主键/唯一键（表级）
            Set<String> pkCols = new LinkedHashSet<>();
            Set<String> uniqueCols = new LinkedHashSet<>();

            for (String line : lines) {
                String t = line.trim();

                // 表级 PRIMARY KEY (...)
                Matcher pkM = PRIMARY_KEY_PATTERN.matcher(t);
                if (pkM.find()) {
                    pkCols.addAll(extractColumnNamesInParens(pkM.group(1)));
                }

                // 表级 UNIQUE KEY / UNIQUE INDEX (...)
                Matcher ukM = UNIQUE_KEY_PATTERN.matcher(t);
                if (ukM.find()) {
                    uniqueCols.addAll(extractColumnNamesInParens(ukM.group(1)));
                }
            }

            // 2.3 标记到字段
            for (CodeTableVo.Column c : codeTableVo.getColumnList()) {
                if (pkCols.contains(c.getName())) {
                    c.setIsPk(1);
                }
                if (uniqueCols.contains(c.getName())) {
                    c.setIsUnique(1);
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
     * 解析单行列定义。只处理以反引号列名开头的行（字段行）。
     * 兼容：NOT NULL / DEFAULT xxx / AUTO_INCREMENT / COMMENT '带空格,逗号' / unsigned / zerofill
     */
    private CodeTableVo.Column parseColumnLine(String line) {
        if (line == null) return null;

        Matcher m = COLUMN_LINE_PATTERN.matcher(line);
        if (!m.find()) {
            return null; // 不是字段行（可能是 PRIMARY KEY/KEY/ENGINE 行等）
        }

        String colName = m.group(1);
        String typeToken = m.group(2);
        String rest = m.group(3) == null ? "" : m.group(3);

        CodeTableVo.Column column = new CodeTableVo.Column();
        column.setName(colName);
        column.setDataType(typeToken);
        column.setIsPk(0);
        column.setIsUnique(0);
        column.setIsZeroFill(0);
        column.setIsAutoIncrement(0);
        column.setIsNotNull(0);

        // unsigned / zerofill
        if (containsWord(rest, "unsigned")) {
            // 你原来把 unsigned 当 unique，这是 bug。
            column.setIsUnique(1);
        }
        if (containsWord(rest, "zerofill")) {
            column.setIsZeroFill(1);
        }

        // NOT NULL / NULL
        if (rest.toUpperCase(Locale.ROOT).contains("NOT NULL")) {
            column.setIsNotNull(1);
        }

        // AUTO_INCREMENT
        if (rest.toUpperCase(Locale.ROOT).contains("AUTO_INCREMENT")) {
            column.setIsAutoIncrement(1);
        }

        // inline PRIMARY KEY（少数写法：`id` int NOT NULL PRIMARY KEY）
        if (rest.toUpperCase(Locale.ROOT).contains("PRIMARY KEY")) {
            column.setIsPk(1);
        }

        // DEFAULT
        Matcher defM = COLUMN_DEFAULT_PATTERN.matcher(rest);
        if (defM.find()) {
            String raw = defM.group(1);
            if (raw != null) {
                raw = raw.trim();
                // 去掉末尾逗号（极少数格式）
                if (raw.endsWith(",")) raw = raw.substring(0, raw.length() - 1).trim();

                if (raw.startsWith("'") && raw.endsWith("'") && raw.length() >= 2) {
                    String v = raw.substring(1, raw.length() - 1);
                    column.setDefaultValue(unescapeMysqlString(v));
                } else {
                    column.setDefaultValue(raw);
                }
            }
        }

        // COMMENT
        Matcher cmtM = COLUMN_COMMENT_PATTERN.matcher(rest);
        if (cmtM.find()) {
            String comment = cmtM.group(1);
            comment = unescapeMysqlString(comment);
            column.setComment(comment);

            if (comment != null) {
                String[] commentParts = comment.split(";");
                if (commentParts.length >= 1) {
                    column.setTitle(commentParts[0]);
                }
            }
        }

        // 解析 length/scale（只对形如 varchar(128) / decimal(9,2) / int(11) 生效；enum/set 会跳过）
        parseLengthAndScale(typeToken, column);

        return column;
    }

    private void parseLengthAndScale(String typeToken, CodeTableVo.Column column) {
        if (StringUtils.isEmpty(typeToken) || column == null) return;

        // enum('a','b') / set(...) 这种不解析数字长度
        String lower = typeToken.toLowerCase(Locale.ROOT);
        if (lower.startsWith("enum(") || lower.startsWith("set(")) {
            // dataType 就保持原样或只取 enum/set
            column.setDataType(lower.startsWith("enum(") ? "enum" : "set");
            return;
        }

        Matcher m = TYPE_NUMERIC_LEN_SCALE_PATTERN.matcher(typeToken);
        if (m.find()) {
            String baseType = m.group(1);
            String len = m.group(2);
            String scale = m.group(3);

            if (!StringUtils.isEmpty(baseType)) {
                column.setDataType(baseType);
            }
            if (!StringUtils.isEmpty(len)) {
                try {
                    column.setLength(Integer.parseInt(len));
                } catch (Exception ignore) {
                }
            }
            if (!StringUtils.isEmpty(scale)) {
                try {
                    column.setScale(Integer.parseInt(scale));
                } catch (Exception ignore) {
                }
            }
        }
    }

    private String extractTableComment(String createTableSql) {
        if (StringUtils.isEmpty(createTableSql)) return "";
        Matcher m = TABLE_COMMENT_PATTERN.matcher(createTableSql);
        if (m.find()) {
            return unescapeMysqlString(m.group(1));
        }
        return "";
    }

    private Set<String> extractColumnNamesInParens(String insideParens) {
        if (insideParens == null) return Collections.emptySet();
        // insideParens 形如：`id`,`user_id`(或带长度 `col`(10) 在索引里也可能出现)
        String[] parts = insideParens.split(",");
        Set<String> cols = new LinkedHashSet<>();
        for (String p : parts) {
            String t = p.trim();
            // 去掉索引前缀长度：`col`(10)
            int parenIdx = t.indexOf('(');
            if (parenIdx > 0) {
                t = t.substring(0, parenIdx).trim();
            }
            t = t.replace("`", "").trim();
            if (!t.isEmpty()) cols.add(t);
        }
        return cols;
    }

    private boolean containsWord(String text, String word) {
        if (text == null || word == null) return false;
        // 简单词边界匹配，避免匹配到其它单词片段
        Pattern p = Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE);
        return p.matcher(text).find();
    }

    private String unescapeMysqlString(String s) {
        if (s == null) return null;
        // MySQL SHOW CREATE TABLE 里常见转义：\'  \\  \n \r \t
        return s.replace("\\'", "'")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    /**
     * 创建sql
     */
    public void execute(String sql) {
        execute(sql, DEFAULT_DB);
    }

    @Override
    public void execute(String sql, String dbName) {
        try {
            if (StringUtils.isEmpty(dbName)) {
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
     */
    public void dropTable(String tableName) {
        dropTable(tableName, DEFAULT_DB);
    }

    @Override
    public void dropTable(String tableName, String dbName) {
        try {
            if (StringUtils.isEmpty(dbName)) {
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
            if (StringUtils.isEmpty(dbName)) {
                dbName = DEFAULT_DB;
            }
            DynamicDataSourceContextHolder.push(dbName);
            String sql = "SHOW TABLES LIKE '" + tableName + "';";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            isExistsTable = list.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return isExistsTable;
    }

    @Override
    public Set<String> getDbList() {
        String prefix = "spring.datasource.dynamic.datasource.";
        try {
            if (!(env instanceof ConfigurableEnvironment)) {
                return Collections.emptySet();
            }
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

            return Arrays.stream(propertyNames)
                    .map(propertyName -> propertyName.substring(prefix.length()))
                    .map(subProperty -> {
                        int firstDotIndex = subProperty.indexOf('.');
                        return (firstDotIndex != -1) ? subProperty.substring(0, firstDotIndex) : subProperty;
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }
}
