package work.soho.common.data.excel.view;

import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import work.soho.common.data.excel.annotation.ExcelColumn;
import work.soho.common.data.excel.model.ExcelModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultExcelView extends AbstractXlsView {

    public Class<?> getActualType(Object o) {
        Type clazz = o.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)clazz;
        return (Class<?>)pt.getActualTypeArguments()[0];
    }

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
            //设置标题栏行高
            header.setHeightInPoints(24);
            //输出excel Title
            // 创建单元格样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();

            // 设置浅蓝色背景颜色 (使用 IndexedColors 枚举)
            titleStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 设置字体大小为14号，并加粗
            titleFont.setFontHeightInPoints((short) 18); // 14号字体
            //TODO 设置字体，会导致跨平台有问题， Linux,window 字体不一样， 会导致显示异常, 加粗对字体有要求
//            titleFont.setBold(true); // 设置加粗
//            titleFont.setFontName("WenQuanYi Micro Hei"); // 设置字体为宋体
            titleStyle.setFont(titleFont);
            // 设置单元格内容居中
            titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

            AtomicInteger hi = new AtomicInteger();
            Arrays.stream(modelClass.getDeclaredFields()).forEach(field -> {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if(excelColumn == null) {
                    return;
                }

                Cell cell = header.createCell(hi.getAndIncrement());
                cell.setCellValue(excelColumn.value());
                cell.setCellStyle(titleStyle);
            });
            // 调整每一列的宽度，使其适应内容
            for (int i = 0; i < hi.get(); i++) {
                sheet.autoSizeColumn(i);
            }

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(rowNumber++);
                int celNum = 0;
                for (int i1 = 0; i1 < modelClass.getDeclaredFields().length; i1++) {
                    Field field = modelClass.getDeclaredFields()[i1];
                    ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                    if(excelColumn == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    try {
                        Object v = field.get(data.get(i));
                        if(v instanceof Date) {
                            CellStyle cellStyle = workbook.createCellStyle();
                            CreationHelper createHelper = workbook.getCreationHelper();
                            short dateFormat = createHelper.createDataFormat().getFormat(excelColumn.dateFormat());
                            cellStyle.setDataFormat(dateFormat);
                            row.createCell(celNum).setCellStyle(cellStyle);
                            row.getCell(celNum).setCellValue((Date) v);
                        } else if(v instanceof Integer) {
                            row.createCell(celNum).setCellValue((Integer)v);
                        }else if(v instanceof Long) {
                            row.createCell(celNum).setCellValue((Long)v);
                        } else {
                            //忽略 null 值
                            if(v != null) {
                                row.createCell(celNum).setCellValue(String.valueOf(v));
                            }

                        }
                        celNum++;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
