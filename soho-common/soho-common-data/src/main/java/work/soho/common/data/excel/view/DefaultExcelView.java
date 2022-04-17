package work.soho.common.data.excel.view;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import work.soho.common.data.excel.annotation.ExcelColumn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultExcelView extends AbstractXlsView {

    public Class<?> getActualType(Object o) {
        Type clazz = o.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)clazz;
        return (Class<?>)pt.getActualTypeArguments()[0];
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Class<Object> objectClass = (Class)model.get("class");
        List<Object> list = (List<Object>) model.get("list");
        Class<?> objectClass = list.get(0).getClass();
//                System.out.println(list.get(0).getClass().getTypeName());

        String fileName = "用户列表excel.xls";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/ms-excel");
        response.setHeader("Content-Disposition", "inline; filename="+new String(fileName.getBytes(),"iso8859-1"));
        OutputStream outputStream = response.getOutputStream();
        // 产生Excel表头
        int rowNumber = 0;
        Sheet sheet = workbook.createSheet("基本信息");
        Row header = sheet.createRow(rowNumber++);
        //输出excel Title
        AtomicInteger hi = new AtomicInteger();
        Arrays.stream(objectClass.getDeclaredFields()).forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            header.createCell(hi.getAndIncrement()).setCellValue(excelColumn.value());
        });

        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(rowNumber++);
            for (int i1 = 0; i1 < objectClass.getDeclaredFields().length; i1++) {
                Field field = objectClass.getDeclaredFields()[i1];
                field.setAccessible(true);
                row.createCell(i1).setCellValue(String.valueOf(field.get(list.get(i))));
            }
        }

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm/dd/yyyy"));

        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}