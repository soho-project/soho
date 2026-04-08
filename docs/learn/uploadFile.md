# 文件服务 (soho-upload)

## 简介

`soho-upload` 是一个高级文件管理服务，它不仅仅处理简单的文件上传，还提供了一套完整的文件生命周期管理方案。该模块的核心目标是高效、可靠地处理文件，并节约服务器存储资源。

### 核心功能

-   **数据库追踪**: 所有上传的文件信息都会被记录在数据库中，便于管理和查询。
-   **引用计数**: 同一个文件（根据文件内容的哈希值判断）在系统中只存储一份。每次上传相同的文件时，仅增加其引用计数，而不是重复存储，极大节省了磁盘空间。
-   **秒传**: 在文件上传前，可通过接口检查文件是否存在。如果已存在，则无需上传文件本身，服务器直接返回文件信息，实现“秒传”。
-   **多渠道支持**: 底层支持多种存储后端，如本地磁盘、OSS、S3 等，可通过配置灵活切换。

## 推荐上传流程

为了最大化利用“秒传”功能并减少不必要的流量，推荐客户端遵循以下流程：

1. **客户端计算哈希**：在上传文件前，客户端计算文件唯一标识，通常是 MD5 或 SHA256。
2. **预检查（Check）**：调用 `POST /client/api/upload/checkUpload`，提交文件指纹等信息。
3. **服务端响应**：
   如果文件已存在，直接返回完整文件信息，客户端无需重复上传。
   如果文件不存在，返回 `null` 或特定未命中标识。
4. **执行上传**：仅在预检查未命中时，调用 `POST /client/api/upload` 上传完整文件。

## API 端点

`soho-upload` 模块通过 `UploadController` 暴露了标准的 RESTful API。

- `POST /client/api/upload/checkUpload`
  功能：预上传检查，用于实现秒传。
  请求体（`UploadInfoVo`）：
        ```json
        {
          "fingerprint": "文件的哈希值, 例如: d41d8cd98f00b204e9800998ecf8427e"
        }
        ```
  响应：如果文件存在，返回完整的 `UploadInfoVo`；否则返回 `null`。

- `POST /client/api/upload`
  功能：上传文件。
  请求参数：`Content-Type` 必须为 `multipart/form-data`，文件参数名必须为 `upload`。
  响应：返回包含文件 URL 与元信息的 `UploadInfoVo`。

## 服务端使用

在其他业务模块中，你可以直接注入 `work.soho.upload.api.Upload` 接口来使用文件服务。

```java
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MyBusinessService {

    private final Upload uploadService;

    // 通过构造函数注入 Upload 接口
    public MyBusinessService(Upload uploadService) {
        this.uploadService = uploadService;
    }

    public String handleFileUpload(MultipartFile file) {
        // 直接调用 save 方法处理文件
        UploadInfoVo uploadInfo = uploadService.save(file);
        
        if (uploadInfo != null) {
            // 返回文件的访问 URL
            return uploadInfo.getUrl();
        }
        return null;
    }
}
```

## 核心接口方法 (`Upload`)

- `UploadInfoVo save(MultipartFile file)`
  处理 HTTP 上传的 `MultipartFile`，这是最常用的方式。

- `UploadInfoVo save(String uri)`
  支持传入标准 HTTP/HTTPS 链接，服务端会自动下载并保存文件。

- `UploadInfoVo checkUploadCache(UploadInfoVo uploadInfoVo)`
  秒传检查入口。通过 `fingerprint` 判断文件是否已存在。

- `void deleteByUrl(String url)`
  根据 URL 删除文件引用。引用计数归零后，底层物理文件才会真正删除。
  当业务对象不再引用某个文件时，应及时调用该接口。

## 配置

文件服务的底层存储是渠道化的，你可以在 `application.yml` 中配置一个或多个存储渠道。

### 本地文件存储示例

以下配置展示了如何定义一个名为 `file` 的渠道，它使用服务器本地磁盘进行存储。

```yaml
upload:
  channels:
    # 'file' 是渠道自定义名称
    file:
      # 'type' 指定底层存储适配器，'file' 代表本地磁盘
      type: file
      config:
        # baseDir: 服务器本地物理路径，需保证目录存在且具备写权限
        baseDir: /data/upload/
        # urlPrefix: 文件访问前缀，最终 URL 通常为 urlPrefix + 相对路径
        urlPrefix: https://cdn.your-domain.com/
```

### 其他渠道

除了 `file` 类型，系统还支持其他多种适配器，如 `oss` (阿里云), `s3` (AWS), `cos` (腾讯云) 等。配置方式与上面类似，只需更改 `type` 和相应的 `config` 即可。
