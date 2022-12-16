package work.soho.code.biz.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private ZipOutputStream zipOutputStream;

    public ZipUtils(String zipFileName) {
        iniZip(zipFileName);
    }

    private void iniZip(String zipFileName) {
        ZipOutputStream zos = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFileName);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        }
    }

    /**
     * 关闭文件流
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if(zipOutputStream != null) {
            zipOutputStream.close();
        }
    }

    /**
     * zip包追加文件
     *
     * @param name
     * @param fileBody
     * @throws Exception
     */
    public void appendFile(String name, String fileBody) throws Exception {
        zipOutputStream.putNextEntry(new ZipEntry(name));
        zipOutputStream.write(fileBody.getBytes());
        zipOutputStream.closeEntry();
    }

}
