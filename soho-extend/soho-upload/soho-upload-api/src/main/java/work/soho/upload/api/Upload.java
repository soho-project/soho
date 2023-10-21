package work.soho.upload.api;

import org.springframework.web.multipart.MultipartFile;
import work.soho.upload.api.vo.UploadInfoVo;

import java.io.File;

public interface Upload {
    UploadInfoVo save(MultipartFile file);

    UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);
}
