package work.soho.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.domain.UploadFile;
import work.soho.mapper.UploadFileMapper;
import work.soho.service.UploadFileService;
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

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
            String hash = getHash((FileInputStream) file.getInputStream());

            //检查文件以前是否上传过
            LambdaQueryWrapper<UploadFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UploadFile::getHash, hash);
            lambdaQueryWrapper.eq(UploadFile::getSize, file.getSize());
            lambdaQueryWrapper.eq(UploadFile::getExtension, extension.toLowerCase());
            UploadFile uploadFile = getOne(lambdaQueryWrapper);

            if(uploadFile == null) {
                String url = UploadUtils.upload("group/avatar", file);

                //保存到数据库
                uploadFile = new UploadFile();
                uploadFile.setFileType(mimeType.toString());
                uploadFile.setExtension(extension);
                uploadFile.setSize(file.getSize());
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
        UploadFile uploadFile = getOne(lambdaQueryWrapper);

        if(uploadFile !=null ) {
            uploadFile.setRefCount(uploadFile.getRefCount()+1);
            uploadFile.setLastRefTime(LocalDateTime.now());
            updateById(uploadFile);
            return BeanUtils.copy(uploadFile, UploadInfoVo.class);
        }

        return null;
    }

    /**
     * 获取文件指纹
     *
     * @param fileInputStream
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getHash(FileInputStream fileInputStream) throws NoSuchAlgorithmException, IOException {
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
}
