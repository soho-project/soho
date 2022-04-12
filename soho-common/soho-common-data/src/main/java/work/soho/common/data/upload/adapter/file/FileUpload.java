package work.soho.common.data.upload.adapter.file;

import lombok.RequiredArgsConstructor;
import work.soho.common.data.upload.Upload;

import java.io.*;

@RequiredArgsConstructor
public class FileUpload implements Upload {
    private final FileProperties fileProperties;

    @Override
    public String uploadFile(String filePath, String content) {
        return uploadFile(filePath, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        OutputStream outputStream = null;
        try {
            String fileFullPath = fileProperties.getBaseDir() + filePath;
            File file = new File(fileFullPath);
            //检查文件夹是否存在
            File dir = new File(file.getParent());
            if(!dir.exists()) {
                dir.mkdirs();
            }
            outputStream = new FileOutputStream(file);
            int bytesWritten = 0;
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            while((byteCount =  inputStream.read(bytes))!=-1) {
                outputStream.write(bytes, bytesWritten, byteCount);
                bytesWritten += byteCount;
            }
            return fileProperties.getUrlPrefix() + filePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                if(outputStream!=null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
