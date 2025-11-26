package work.soho.common.data.excel.view;

import com.alibaba.excel.annotation.ExcelProperty;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import work.soho.common.data.excel.model.ExcelModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultExcelView extends AbstractXlsView {
    @Override
    protected void buildExcelDocument(@NotNull Map<String, Object> model, Workbook workbook, @NotNull HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExcelModel excelModel = new ExcelModel();
        excelModel.putAll(model);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename="+new String(excelModel.getFileName().getBytes(),"iso8859-1"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        OutputStream outputStream = response.getOutputStream();
        excelModel.getSheetList().forEach(sheetOriginData -> {
            HashMap<String, Object> sheetData = (HashMap<String, Object>)sheetOriginData;
            String sheetName = (String) sheetData.get(ExcelModel.SHEET_NAME);
            Class<?> modelClass = (Class<?>) sheetData.get(ExcelModel.SHEET_CLASS);
            List<?> data = (List<?>) sheetData.get(ExcelModel.SHEET_DATA);

            // 产生Excel表头
            int rowNumber = 0;
            Sheet sheet = workbook.createSheet(sheetName);
            Row header = sheet.createRow(rowNumber++);

            // 设置标题栏行高
            header.setHeightInPoints(22); // 稍微降低行高，更紧凑

            // 创建优化的表头样式
            CellStyle titleStyle = createHeaderStyle(workbook);

            AtomicInteger hi = new AtomicInteger();
            List<Field> excelFields = getExcelFields(modelClass);

            // 先创建所有表头单元格
            excelFields.forEach(field -> {
                ExcelProperty excelColumn = field.getAnnotation(ExcelProperty.class);
                Cell cell = header.createCell(hi.getAndIncrement());
                cell.setCellValue(excelColumn.value()[0]);
                cell.setCellStyle(titleStyle);
            });

            // 优化列宽设置
            optimizeColumnWidth(sheet, excelFields.size());

            // 填充数据行
            fillDataRows(workbook, sheet, data, modelClass, rowNumber);
        });

        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 创建优化的表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle titleStyle = workbook.createCellStyle();

        // 设置背景颜色 - 使用更柔和的蓝色
        titleStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置边框 - 添加细边框
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        titleStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        titleStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        titleStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

        // 设置字体 - 跨平台安全的字体设置
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 12); // 适中的字体大小
        titleFont.setBold(true); // 加粗 - 大多数平台都支持
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex()); // 深蓝色字体

        // 避免使用特定字体名称，使用默认字体
        // titleFont.setFontName(""); // 不设置字体名称，使用系统默认

        titleStyle.setFont(titleFont);

        // 设置对齐方式
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置文本自动换行
        titleStyle.setWrapText(true);

        return titleStyle;
    }

    /**
     * 获取带有ExcelProperty注解的字段
     */
    private List<Field> getExcelFields(Class<?> modelClass) {
        List<Field> excelFields = new ArrayList<>();
        for (Field field : modelClass.getDeclaredFields()) {
            ExcelProperty excelColumn = field.getAnnotation(ExcelProperty.class);
            if (excelColumn != null) {
                excelFields.add(field);
            }
        }
        return excelFields;
    }

    /**
     * 优化列宽设置
     */
    private void optimizeColumnWidth(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            // 设置最小列宽
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < 3000) { // 约3个字符宽度
                sheet.setColumnWidth(i, 4000); // 约4个字符宽度
            } else if (currentWidth > 15000) { // 防止过宽
                sheet.setColumnWidth(i, 15000);
            }
        }
    }

    /**
     * 填充数据行
     */
    private void fillDataRows(Workbook workbook, Sheet sheet, List<?> data,
                              Class<?> modelClass, int startRow) {
        List<Field> excelFields = getExcelFields(modelClass);
        CellStyle dataCellStyle = createDataCellStyle(workbook);
        CellStyle dateCellStyle = createDateCellStyle(workbook);

        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(startRow + i);
            int cellNum = 0;

            for (Field field : excelFields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(data.get(i));
                    Cell cell = row.createCell(cellNum);

                    // 设置单元格样式
                    if (value instanceof Date || value instanceof LocalDateTime) {
                        cell.setCellStyle(dateCellStyle);
                    } else {
                        cell.setCellStyle(dataCellStyle);
                    }

                    // 设置单元格值
                    setCellValue(cell, value);
                    cellNum++;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建数据单元格样式
     */
    private CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        // 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 创建日期单元格样式
     */
    private CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();

        // 设置日期格式
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd HH:mm:ss");
        style.setDataFormat(dateFormat);

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        // 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 设置单元格值
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            cell.setCellValue(date);
        } else if (value instanceof Number) {
            if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Float) {
                cell.setCellValue((Float) value);
            } else {
                cell.setCellValue(value.toString());
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}