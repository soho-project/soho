package work.soho.controller.client;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.core.result.R;
import work.soho.service.UploadFileService;
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;

@Api(tags = "客户端上传文件")
@RestController
@Log4j2
@RequestMapping("/client/api/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadFileService uploadFileService;
    private final Upload upload;

    @PostMapping
    public R<UploadInfoVo> upload(@RequestParam(value = "upload") MultipartFile file) {
        try {
            UploadInfoVo uploadInfoVo = upload.save(file);
            Assert.notNull(uploadInfoVo, "上传失败");
            return R.success(uploadInfoVo);
        } catch (Exception ioException) {
            log.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }

    /**
     * 预上传检查
     *
     * @param uploadInfoVo
     * @return
     */
    @PostMapping("/checkUpload")
    public R<UploadInfoVo> uploadCache(@RequestBody UploadInfoVo uploadInfoVo) {
        UploadInfoVo res = upload.checkUploadCache(uploadInfoVo);
        return R.success(res);
    }
}
