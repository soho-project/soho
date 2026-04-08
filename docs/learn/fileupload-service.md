# 文件上传服务

该服务对文件上传做了统一业务封装，用于：

- 统一文件存储入口
- 保存文件元数据
- 支持秒传与缓存检查

## 核心接口

```java
public interface Upload {
    UploadInfoVo save(MultipartFile file);

    UploadInfoVo save(String uri);

    UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo);
}
```

## 说明

- `save(MultipartFile file)`：上传本地文件对象。
- `save(String uri)`：通过 URI 方式保存文件。
- `checkUploadCache(UploadInfoVo uploadInfoVo)`：用于检查是否命中已有上传记录。
