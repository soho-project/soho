package work.soho.admin.api.request;


import lombok.Data;

@Data
public class AiChatRequest {
    private String mode;        // rewrite/expand/summarize/translate/fix/generate...
    private String lang;        // zh/en/ja
    private String prompt;      // 用户附加要求
    private String selectedText;
    private String fullText;
    private String systemPrompt;
}
