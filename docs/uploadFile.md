# 文件上传模块

业务使用首选业务上传模块进行文件上传，该模块对上传进行了业务封装，对上传的文件进行了数据库相关文件特征存储；会对已经上传过的文件进行引用计数方式减少系统磁盘存储。


# 相关接口


    public interface Upload {
        UploadInfoVo save(MultipartFile file);
    
        UploadInfoVo save(String uri);
    
        UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);
    }

## UploadInfoVo save(String uri);

    该函数支持传递一个标准的http or https地址进行文件保存。

## UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo)

    上传之前应该调用该接口问询文件是否存在，可以极大降低文件上传下载流量；实现文件秒传。
