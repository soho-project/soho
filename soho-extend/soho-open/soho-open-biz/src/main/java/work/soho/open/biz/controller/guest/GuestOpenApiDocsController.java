package work.soho.open.biz.controller.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.ErrorCode;
import work.soho.common.core.result.ErrorCodeCollector;
import work.soho.common.core.result.R;
import work.soho.open.biz.service.impl.ControllerApiReaderServiceImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest/open/api-docs" )
public class GuestOpenApiDocsController {
    private final ControllerApiReaderServiceImpl controllerApiReaderService;
    @GetMapping
    public R<String> getApiDocs() throws JsonProcessingException {
        String md = controllerApiReaderService.printAsMarkdown(controllerApiReaderService.getAllControllerApis());
        return R.success(md);
    }

    @GetMapping("md")
    public String getApiDocsMd() throws JsonProcessingException {
        String md = controllerApiReaderService.printAsMarkdown(controllerApiReaderService.getAllControllerApis());
        return md;
    }

    /**
     * 获取指定类型文档信息
     *
     * @param categoryKey
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("getByCategoryKey")
    public R<Map<String, String>> getApiDocsByCategoryKey(String categoryKey) throws JsonProcessingException {
        switch (categoryKey) {
            case "api-docs":
                String md = controllerApiReaderService.printAsMarkdown(controllerApiReaderService.getAllControllerApis());
                HashMap<String, String> mdMap = new HashMap<>();
                mdMap.put("viewType", "docs");
                mdMap.put("data", md);
                return R.success(mdMap);
            case "api-errorCode":
                List<ErrorCode> errorCodeList = ErrorCodeCollector.collectErrorCodeList("work.soho.");
                HashMap<String, String> errorCodeMap = new HashMap<>();
                errorCodeMap.put("viewType", "errorCode");
                errorCodeMap.put("data", generateErrorCodeMarkdown(errorCodeList));

                return R.success(errorCodeMap);
        }
        return R.error();
    }

    @GetMapping("getByErrorCodeInfo")
    public R<List<HashMap<String, Object>>> getByErrorCodeInfo() {
        List<ErrorCode> errorCodeList = ErrorCodeCollector.collectErrorCodeList("work.soho.");
        List<HashMap<String, Object> > list = errorCodeList.stream().map(errorCode -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", errorCode.code());
            map.put("message", errorCode.message());
            map.put("description", errorCode.description());
            return map;
        }).collect(Collectors.toList());
        return R.success(list);
    }

    private static String generateErrorCodeMarkdown(List<? extends ErrorCode> errorCodes) {
        StringBuilder md = new StringBuilder();

        // 文档头
        md.append("# 开放平台错误码说明\n\n")
                .append("> 本文档用于说明开放平台 API 在调用过程中可能返回的错误码及其含义，便于开发者快速定位和解决问题。\n\n");

        md.append("---\n");

        // 添加二级标题
        md.append("## 错误码列表\n\n");

        // 表头
        md.append("| 错误码 | 错误信息 | 详细描述 |\n")
                .append("|--------|----------|----------|\n");

        // errorCodes 根据  ErrorCode.code() 排序, 升序
        List<ErrorCode> sortedErrorCodes = errorCodes.stream().sorted(Comparator.comparingInt(ErrorCode::code)).collect(Collectors.toList());

        // 内容
        for (ErrorCode errorCode : sortedErrorCodes) {
            md.append("| ")
                    .append(errorCode.code()).append(" | ")
                    .append(escape(errorCode.message())).append(" | ")
                    .append(escape(errorCode.description())).append(" |\n");
        }

        // 添加使用建议
        md.append("## 使用建议\n" +
                "\n" +
                "- 当 `code != 2000` 时，应根据错误码进行相应的异常处理  \n" +
                "- 建议对 **600x / 610x** 错误进行重点日志记录  \n" +
                "- 对于 **6104** 错误，建议实现限流或重试机制  \n" +
                "\n" +
                "---");

        // 写入文件
        return md.toString();
    }

    /**
     * 防止 Markdown 表格被破坏
     */
    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("|", "\\|")
                .replace("\n", " ");
    }
}
