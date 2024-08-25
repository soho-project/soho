package work.soho.upload.api;

import org.springframework.web.multipart.MultipartFile;
import work.soho.upload.api.vo.UploadInfoVo;

public interface Upload {
    UploadInfoVo save(MultipartFile file);

    UploadInfoVo save(String uri);

    UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);

    void deleteByUrl(String url);
}
