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
     * 模板分组ID
     *
     * templateId 二选一
     */
    private Integer groupId;

    /**
     * 模板ID
     *
     * groupId 二选一
     */
    private ArrayList<Integer> templateId = new ArrayList<>();

    /**
     * 存储基本路径
     */
    private String path;

    /**
     * 代码基本命名空间
     */
    private String codeNamespace;

    /**
     * 模块名称
     */
    private String moduleName;
}
