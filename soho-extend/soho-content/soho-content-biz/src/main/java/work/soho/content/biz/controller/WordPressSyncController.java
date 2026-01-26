package work.soho.content.biz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import work.soho.common.core.result.R;
import work.soho.content.biz.wordpress.WordPressExportService;
import work.soho.content.biz.wordpress.WordPressSyncService;
import work.soho.content.biz.wordpress.WordPressWxrImportService;
import work.soho.content.biz.wordpress.WordPressWxrExportService;
import work.soho.content.biz.wordpress.dto.WordPressExportResult;
import work.soho.content.biz.wordpress.dto.WordPressSyncRequest;
import work.soho.content.biz.wordpress.dto.WordPressSyncResult;
import work.soho.content.biz.wordpress.dto.WordPressWxrImportResult;

/**
 * WordPress 导入导出管理接口
 */
@RestController
@RequestMapping("/content/admin/wordpress")
public class WordPressSyncController {
    private final WordPressSyncService syncService;
    private final WordPressExportService exportService;
    private final WordPressWxrImportService wxrImportService;
    private final WordPressWxrExportService wxrExportService;

    public WordPressSyncController(WordPressSyncService syncService,
                                   WordPressExportService exportService,
                                   WordPressWxrImportService wxrImportService,
                                   WordPressWxrExportService wxrExportService) {
        this.syncService = syncService;
        this.exportService = exportService;
        this.wxrImportService = wxrImportService;
        this.wxrExportService = wxrExportService;
    }

    @PostMapping("/import")
    public R<WordPressSyncResult> importFromWordPress(@RequestBody WordPressSyncRequest request) {
        return R.success(syncService.importFromWordPress(request));
    }

    @PostMapping("/import-wxr")
    public R<WordPressWxrImportResult> importWxr(@RequestParam("file") MultipartFile file) {
        return R.success(wxrImportService.importWxr(file));
    }

    @GetMapping("/export")
    public R<WordPressExportResult> exportToWordPress() {
        return R.success(exportService.exportAll());
    }

    @GetMapping("/export-wxr")
    public ResponseEntity<byte[]> exportWxr() {
        byte[] body = wxrExportService.exportWxr();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDispositionFormData("attachment", "wordpress-export.xml");
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
