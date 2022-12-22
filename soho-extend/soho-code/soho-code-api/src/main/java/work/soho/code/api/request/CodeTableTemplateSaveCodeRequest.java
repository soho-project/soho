package work.soho.code.api.request;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CodeTableTemplateSaveCodeRequest {
    /**
     * 表模型ID
     */
    private Integer id;

    /**
     * 模板ID
     */
    private ArrayList<Integer> templateId;

    /**
     * 存储基本路径
     */
    private String path;

    /**
     * 代码基本命名空间
     */
    private String codeNamespace;
}
