package work.soho.open.biz.controller.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.open.biz.service.impl.ControllerApiReaderServiceImpl;

import java.util.HashMap;
import java.util.Map;

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
        }
        return R.error();
    }
}
