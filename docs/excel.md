# Excel 相关操作


## 导出excel表格

### 定义模型

定义一个简单类，属性添加注解 ExcelProperty 标记相关信息（标题，时间格式化）

    @Data
    public class Hello implements Serializable {
    
        @ExcelProperty(value = "ID")
        private Integer id;
    
        @ExcelProperty("名称")
        private String name;
    
        @ExcelProperty("值")
        private String value;

        @ExcelProperty("更新时间")
        private Date updatedTime;
    
        @ExcelProperty(value = "创建时间", dateFormat = "yyyy-dd-MM")
        private Date createdTime;
    }

### 方式一

    @GetMapping("/hello/excel")
    @ExcelExport(fileName = "test.xsl", modelClass = Hello.class)
    public Object exportHelloList() {
        List<Hello> list = helloService.list();
        return list;
    }

### 方式二

    @GetMapping("/hello/excel2")
    public ModelAndView excel() {
        List<Hello> list = helloService.list();
        ExcelModel excelModel = new ExcelModel();
        excelModel.setFileName("test.xls").addSheet(list, Hello.class).addSheet(list, Hello.class);
        return new ModelAndView(new DefaultExcelView(), excelModel);
    }

### 方式三

    @GetMapping("/hello/excel3")
    @ExcelExport(fileName = "test.xsl", modelClass = Hello.class)
    public Object exportHelloList2() {
        List<Hello> list = helloService.list();
        return new ExcelModel().addSheet(list);
    }

