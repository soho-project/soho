package work.soho.upload.api.vo;

import lombok.Data;

@Data
public class UploadInfoVo {
    /**
     * 文件URL地址
     */
    private String url;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件hash/指纹
     */
    private String hash;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展
     */
    private String extension;
}
