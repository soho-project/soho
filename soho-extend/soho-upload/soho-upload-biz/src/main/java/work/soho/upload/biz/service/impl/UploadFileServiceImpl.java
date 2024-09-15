package work.soho.upload.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.upload.biz.domain.UploadFile;
import work.soho.upload.biz.mapper.UploadFileMapper;
import work.soho.upload.biz.service.UploadFileService;
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile>
    implements UploadFileService, Upload {

    @Override
    public UploadInfoVo save(MultipartFile file) {
        try {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            //检查文件的指纹
            String hash = getHash(file.getInputStream());

            return upload(file.getInputStream(), mimeType.getType(), extension,hash, file.getSize());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UploadInfoVo save(String uri) {
        try {
            //检查字符串是否为base64图片
            if(uri.startsWith("data:image/")) {
                // 解码数据URI
                String base64Data = uri.split(",")[1];
                byte[] decodedData = Base64.getDecoder().decode(base64Data);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedData);
                String hash = getHash(new ByteArrayInputStream(decodedData));
                String mimeType = getFileMimeType(uri);
                String extension = getFileExtension(uri);
                return upload(inputStream, mimeType, extension, hash, Long.valueOf(decodedData.length));
            } else if(isValidURL(uri)) {
                //URL地址上传，URL地址如果非本站地址则下载后重新上传
                LambdaQueryWrapper<UploadFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadFile::getUrl, uri);
                lambdaQueryWrapper.last("limit 1");
                UploadFile uploadFile = getOne(lambdaQueryWrapper);
                if(uploadFile != null) {
                    //本站已经上传的图片，更新引用数直接返回即可
                    uploadFile.setRefCount(uploadFile.getRefCount()+1);
                    uploadFile.setCreatedTime(LocalDateTime.now());
                    updateById(uploadFile);
                    return BeanUtils.copy(uploadFile, UploadInfoVo.class);
                }
                //下载URL 上传文件
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String contentType = connection.getContentType();
                    String fileExtension = contentType.substring(contentType.indexOf("/")+1);
                    int fileSize = connection.getContentLength();

                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byte[] byteArray = baos.toByteArray();

                    String hash = getHash(new ByteArrayInputStream(byteArray));

                    return upload(new ByteArrayInputStream(byteArray), contentType, fileExtension, hash, Long.valueOf(fileSize));
                } else {
                    throw new IOException("HTTP Request Failed with Response Code: " + responseCode);
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传文件流
     *
     * @param inputStream
     * @param extension
     * @return
     */
    public UploadInfoVo upload(InputStream inputStream,String mimeType, String extension, String hash, Long size) {
        try {
            //检查文件以前是否上传过
            LambdaQueryWrapper<UploadFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UploadFile::getHash, hash);
            lambdaQueryWrapper.eq(UploadFile::getSize, size);
            lambdaQueryWrapper.eq(UploadFile::getExtension, extension.toLowerCase());
            lambdaQueryWrapper.last("limit 1");
            UploadFile uploadFile = getOne(lambdaQueryWrapper);

            if(uploadFile == null) {
                String basePath = "upload/" + hash.substring(0,2) + "/" + hash.substring(2, 4) + "/" + hash.substring(4, 6);
                basePath += hash + "." + extension;
                String url = UploadUtils.upload(basePath, inputStream);

                //保存到数据库
                uploadFile = new UploadFile();
                uploadFile.setFileType(mimeType.toString());
                uploadFile.setExtension(extension);
                uploadFile.setSize(size);
                uploadFile.setHash(hash);
                uploadFile.setCreatedTime(LocalDateTime.now());
                uploadFile.setUpdatedTime(LocalDateTime.now());
                uploadFile.setLastRefTime(LocalDateTime.now());
                uploadFile.setUrl(url);
                uploadFile.setRefCount(1);
                save(uploadFile);
            } else {
                uploadFile.setRefCount(uploadFile.getRefCount()+1);
                uploadFile.setLastRefTime(LocalDateTime.now());
                updateById(uploadFile);
            }

            UploadInfoVo uploadInfoVo = BeanUtils.copy(uploadFile, UploadInfoVo.class);
            return uploadInfoVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo) {
        LambdaQueryWrapper<UploadFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UploadFile::getHash, uploadInfoVo.getHash());
        lambdaQueryWrapper.eq(UploadFile::getSize, uploadInfoVo.getSize());
        lambdaQueryWrapper.eq(UploadFile::getExtension, uploadInfoVo.getExtension().toLowerCase());
        lambdaQueryWrapper.last("limit 1");
        UploadFile uploadFile = getOne(lambdaQueryWrapper);

        if(uploadFile !=null ) {
            uploadFile.setRefCount(uploadFile.getRefCount()+1);
            uploadFile.setLastRefTime(LocalDateTime.now());
            updateById(uploadFile);
            return BeanUtils.copy(uploadFile, UploadInfoVo.class);
        }

        return null;
    }

    @Override
    public void deleteByUrl(String url) {
        UploadFile uploadFile = getOne(new LambdaQueryWrapper<UploadFile>().eq(UploadFile::getUrl, url));
        if(uploadFile != null) {
            uploadFile.setRefCount(uploadFile.getRefCount()>0 ? uploadFile.getRefCount()-1 : 0);
            updateById(uploadFile);
        }
    }

    /**
     * 获取文件指纹
     *
     * @param fileInputStream
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getHash(InputStream fileInputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            md.update(buffer, 0, length);
        }
        fileInputStream.close();
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 检查是否为URL地址
     *
     * @param urlString
     * @return
     */
    public boolean isValidURL(String urlString) {
        try {
            // 尝试创建一个URL对象
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 获取扩展名
     *
     * @param dataURI
     * @return
     */
    public String getFileExtension(String dataURI) {
        // 使用正则表达式匹配文件扩展名
        Pattern pattern = Pattern.compile("data:image/([a-zA-Z]+);base64,");
        Matcher matcher = pattern.matcher(dataURI);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 获取文件 mime type
     *
     * @param dataURI
     * @return
     */
    public String getFileMimeType(String dataURI) {
        // 使用正则表达式匹配文件扩展名
        Pattern pattern = Pattern.compile("data:([a-zA-Z/]+);base64,");
        Matcher matcher = pattern.matcher(dataURI);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
