package work.soho.common.data.excel.view;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
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
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExcelModel excelModel = new ExcelModel();
        model.forEach((key, value)->{
            excelModel.put(key, value);
        });

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/ms-excel");
        response.setHeader("Content-Disposition", "inline; filename="+new String(excelModel.getFileName().getBytes(),"iso8859-1"));
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
            //输出excel Title
            AtomicInteger hi = new AtomicInteger();
            Arrays.stream(modelClass.getDeclaredFields()).forEach(field -> {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if(excelColumn == null) {
                    return;
                }
                header.createCell(hi.getAndIncrement()).setCellValue(excelColumn.value());
            });

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
                            row.createCell(celNum).setCellValue(String.valueOf(v));
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