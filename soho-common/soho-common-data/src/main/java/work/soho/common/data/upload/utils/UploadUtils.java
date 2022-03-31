package work.soho.common.data.upload.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import work.soho.common.data.upload.Upload;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class UploadUtils {
    @Autowired
    private Upload uploadService;

    private static UploadUtils uploadUtils;

    @PostConstruct
    public void init() {
        uploadUtils = this;
        uploadUtils.uploadService = this.uploadService;
    }

    /**
     * 上传字符内容文件
     *
     * @param filePath
     * @param fileContent
     * @return
     */
    public static String upload(String filePath, String fileContent) {
        return uploadUtils.uploadService.uploadFile(filePath, fileContent);
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @param inputStream
     * @return
     */
    public static String upload(String filePath, InputStream inputStream) {
        return uploadUtils.uploadService.uploadFile(filePath, inputStream);
    }
}
