package work.soho.common.data.upload.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.data.upload.Upload;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

@Service
public class UploadUtils {
    @Autowired
    private Upload uploadService;

    private static UploadUtils uploadUtils;

    private static final Logger logger = LoggerFactory.getLogger(UploadUtils.class);

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

    /**
     * 上产文件
     *
     * @param dirPath
     * @param file
     * @return
     */
    public static String upload(String dirPath, MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        String ext = originFileName.substring(originFileName.lastIndexOf("."));
        dirPath = dirPath + "/" + generateRandomFilename() + ext;
        try {
            return upload(dirPath, file.getInputStream());
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
        String RandomFilename = "";
        Random rand = new Random();//生成随机数
        int random = rand.nextInt();

        Calendar calCurrent = Calendar.getInstance();
        int intDay = calCurrent.get(Calendar.DATE);
        int intMonth = calCurrent.get(Calendar.MONTH) + 1;
        int intYear = calCurrent.get(Calendar.YEAR);
        String now = String.valueOf(intYear) + "_" + String.valueOf(intMonth) + "_" +
                String.valueOf(intDay) + "_";
        RandomFilename = now + String.valueOf(random > 0 ? random : ( -1) * random);
        return RandomFilename;
    }
}
