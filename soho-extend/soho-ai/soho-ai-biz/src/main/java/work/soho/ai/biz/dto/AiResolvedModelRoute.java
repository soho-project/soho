package work.soho.ai.biz.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型路由解析结果。
 */
@Data
public class AiResolvedModelRoute {
    /**
     * 客户端请求模型。
     */
    private String requestedModel;

    /**
     * 实际调用模型。
     */
    private String actualModel;

    /**
     * 是否命中兜底。
     */
    private boolean fallbackApplied;

    /**
     * 兜底链路。
     */
    private List<String> fallbackChain = new ArrayList<>();
}
