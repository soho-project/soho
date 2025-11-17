package work.soho.common.data.upload.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.data.upload.UploadManage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

@Service
public class UploadUtils {
    @Autowired
    private UploadManage uploadManage;

    private static UploadUtils uploadUtils;

    @PostConstruct
    public void init() {
        uploadUtils = this;
        uploadUtils.uploadManage = this.uploadManage;
    }

    /**
     * 上传字符内容文件
     *
     * @param filePath
     * @param fileContent
     * @return
     */
    public static String upload(String filePath, String fileContent) {
        return upload(uploadUtils.uploadManage.getDefaultChannelName(), filePath, fileContent);
    }

    /**
     * 上传字符串内容到指定存储桶
     *
     * @param channelName
     * @param filePath
     * @param fileContent
     * @return
     */
    public static String upload(String channelName, String filePath, String fileContent) {
        return uploadUtils.uploadManage.get(channelName).uploadFile(filePath, fileContent);
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @param inputStream
     * @return
     */
    public static String upload(String filePath, InputStream inputStream) {
        return upload(uploadUtils.uploadManage.getDefaultChannelName(), filePath, inputStream);
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @param inputStream
     * @return
     */
    public static String upload(String channelName, String filePath, InputStream inputStream) {
        return uploadUtils.uploadManage.get(channelName).uploadFile(filePath, inputStream);
    }

    /**
     * 上产文件
     *
     * @param dirPath
     * @param file
     * @return
     */
    public static String upload(String dirPath, MultipartFile file) {
        return upload(uploadUtils.uploadManage.getDefaultChannelName(), dirPath, file);
    }

    /**
     * 上传文件到指定通道
     *
     * @param channelName
     * @param dirPath
     * @param file
     * @return
     */
    public static String upload(String channelName, String dirPath, MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        if(originFileName == null) {
            return null;
        }

        // fixed 有些客户端上传的文件，文件名没有扩展名
        String ext = "bin";
        if(originFileName.lastIndexOf(".")>0) {
            ext = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        } else {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            ext = mimeType.getSubtype();
        }

        dirPath = dirPath + "/" + generateRandomFilename() + "." + ext;
        try {
            return upload(channelName, dirPath, file.getInputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    /**
     * 生成随机文件名
     *
     * @return
     */
    public static String generateRandomFilename(){
        Calendar calCurrent = Calendar.getInstance();
        int intDay = calCurrent.get(Calendar.DATE);
        int intMonth = calCurrent.get(Calendar.MONTH) + 1;
        int intYear = calCurrent.get(Calendar.YEAR);
        String now = intYear + "_" + intMonth + "_" + intDay + "_";
        return now + IDGeneratorUtils.snowflake();
    }
}
