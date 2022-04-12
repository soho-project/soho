package work.soho.common.data.upload;

import java.io.IOException;
import java.io.InputStream;

public interface Upload {
    String uploadFile(String filePath, String content);
    String uploadFile(String filePath, InputStream inputStream);
}
