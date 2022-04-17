package work.soho.common.data.excel.model;

import java.util.*;

/**
 * 设置表格数据
 */
public class ExcelModel extends LinkedHashMap<String, Object> {
    public static final String FILE_NAME = "file_name";
    public static final String SHEET_LIST = "sheet_list";
    public static final String SHEET_NAME = "sheet_name";
    public static final String SHEET_DATA = "sheet_data";
    public static final String SHEET_CLASS = "sheet_class";

    /**
     * 添加sheet
     *
     * @param data
     * @return
     */
    public ExcelModel addSheet(List<?> data) {
        return addSheet(data, null);
    }

    public ExcelModel setFileName(String fileName) {
        put(FILE_NAME, fileName);
        return this;
    }

    public String getFileName() {
        return (String)(get(FILE_NAME) == null ? "soho.xsl" : get(FILE_NAME));
    }

    /**
     * 获取sheet列表
     *
     * @return
     */
    public List<Object> getSheetList() {
        return (List<Object>) get(SHEET_LIST);
    }

    /**
     * 设置表格数据
     *
     * @param data
     * @return
     */
    public ExcelModel addSheet(List<?> data, Class<?> rowClass, String name) {
        List<Object> sheetList = (List<Object>) get(SHEET_LIST);

        if(name == null) {
            if(sheetList == null) {
                name = "Sheet";
            } else {
                name = "Sheet" + (sheetList.size() + 1);
            }
        }

        if(sheetList == null) {
            sheetList = new ArrayList<>();
        }
        HashMap<String, Object> sheet = new HashMap<>();
        sheet.put(SHEET_NAME, name);
        sheet.put(SHEET_DATA, data);
        sheet.put(SHEET_CLASS, rowClass);
        sheetList.add(sheet);

        put(SHEET_LIST, sheetList);
        return this;
    }

    public ExcelModel addSheet(List<?> data, Class<?> rowClass) {
        return addSheet(data, rowClass, null);
    }
}
