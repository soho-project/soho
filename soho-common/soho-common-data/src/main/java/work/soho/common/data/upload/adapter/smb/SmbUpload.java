package work.soho.common.data.upload.adapter.smb;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import work.soho.common.data.upload.Upload;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

@Log4j2
@RequiredArgsConstructor
public class SmbUpload implements Upload {

    private final SmbProperties smbProperties;

    @Override
    public String uploadFile(String filePath, String content) {
        return uploadFile(filePath, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        SMBClient client = new SMBClient();

        //查询所有的文件
        try (Connection connection = client.connect(smbProperties.getHostName())) {
            //读取文件
            AuthenticationContext ac = new AuthenticationContext(smbProperties.getUsername(), smbProperties.getPassword().toCharArray(), smbProperties.getDomain());
            Session session = connection.authenticate(ac);
            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare(smbProperties.getShareName())) {
                Path path = Paths.get(filePath);
                Path directoryPath = path.getParent();
                //检查创建目录
                if(!share.folderExists(directoryPath.toString())) {
                    mkdirs(share, directoryPath);
                }
                File file = share.openFile(
                        filePath,
                        EnumSet.of(AccessMask.GENERIC_WRITE),
                        EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN_IF,
                        EnumSet.noneOf(SMB2CreateOptions.class)
                );
                OutputStream out = file.getOutputStream(false);
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    out.flush();
                    out.close();
                    file.close();
                }
                return smbProperties.getUrlPrefix() + filePath;
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return null;
    }

    /**
     * 递归创建目录
     *
     * @param share
     * @param path
     */
    private void mkdirs(DiskShare share, Path path) {
        Path parentPath = path.getParent();
        if(parentPath != null) {
            mkdirs(share, parentPath);
        }
        //检查路径是否存在
        if(share.folderExists(path.toString())) {
            return;
        }
        share.mkdir(path.toString());
    }
}
