文件上传服务
==========

    队文件进行统一业务封装，进行文件数据存储，优化实现文件秒传


接口
---

    public interface Upload {
        UploadInfoVo save(MultipartFile file);
    
        UploadInfoVo save(String uri);
    
        UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);
    }
