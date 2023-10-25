package work.soho.upload.api;

import org.springframework.web.multipart.MultipartFile;
import work.soho.upload.api.vo.UploadInfoVo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface Upload {
    UploadInfoVo save(MultipartFile file);

    UploadInfoVo save(String uri);

    UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);
}
