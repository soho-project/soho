package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiPromptTemplate;
import work.soho.ai.biz.dto.AiPromptRenderResult;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiPromptTemplateService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AiPromptRenderServiceImplTest {

    @Test
    public void render_whenTemplateMatched_shouldMergeInstructionsAndRewriteInput() {
        AiPromptTemplateService aiPromptTemplateService = Mockito.mock(AiPromptTemplateService.class);
        AiPromptRenderServiceImpl service = new AiPromptRenderServiceImpl(aiPromptTemplateService);

        AiPromptTemplate template = new AiPromptTemplate();
        template.setId(11L);
        template.setCode("customer.service");
        template.setSceneCode("customer_service");
        template.setVersion(3);
        template.setSystemPrompt("你是{{role_name}}，当前时间{{current_date}}");
        template.setUserPromptTemplate("请用{{reply_language}}回复：{{input}}");
        when(aiPromptTemplateService.findActiveTemplate("customer.service", "customer_service", "deepseek-prod", "deepseek-chat"))
                .thenReturn(template);

        AiChatRequest request = new AiChatRequest();
        request.setTemplateCode("customer.service");
        request.setSceneCode("customer_service");
        request.setProviderCode("deepseek-prod");
        request.setModel("deepseek-chat");
        request.setInput("订单什么时候发货");
        request.setInstructions("回答要简洁");
        request.setPromptVars(Map.of("role_name", "客服助手", "reply_language", "中文"));

        AiPromptRenderResult result = service.render(request);

        assertThat(result.getTemplateId()).isEqualTo(11L);
        assertThat(result.getRenderedRequest().getInstructions()).contains("你是客服助手");
        assertThat(result.getRenderedRequest().getInstructions()).contains("附加要求");
        assertThat(result.getRenderedRequest().getInput()).isEqualTo("请用中文回复：订单什么时候发货");
        assertThat(result.getRenderedRequest().getSceneCode()).isEqualTo("customer_service");
        assertThat(result.getRenderedRequest().getTemplateCode()).isEqualTo("customer.service");
    }

    @Test
    public void render_whenMessagesMode_shouldRewriteLatestUserMessage() {
        AiPromptTemplateService aiPromptTemplateService = Mockito.mock(AiPromptTemplateService.class);
        AiPromptRenderServiceImpl service = new AiPromptRenderServiceImpl(aiPromptTemplateService);

        AiPromptTemplate template = new AiPromptTemplate();
        template.setId(12L);
        template.setCode("summary.default");
        template.setSceneCode("summary");
        template.setVersion(1);
        template.setSystemPrompt("你是总结助手");
        template.setUserPromptTemplate("请把下面内容总结成三点：{{input}}");
        when(aiPromptTemplateService.findActiveTemplate(null, "summary", null, null)).thenReturn(template);

        AiChatRequest.Message assistant = new AiChatRequest.Message();
        assistant.setRole("assistant");
        assistant.setContent("上一轮回答");
        AiChatRequest.Message user = new AiChatRequest.Message();
        user.setRole("user");
        user.setContent("这里是一段很长的业务文本");

        AiChatRequest request = new AiChatRequest();
        request.setSceneCode("summary");
        request.setMessages(List.of(assistant, user));

        AiPromptRenderResult result = service.render(request);

        assertThat(result.getRenderedRequest().getInput()).isNull();
        assertThat(result.getRenderedRequest().getMessages().get(1).getContent())
                .isEqualTo("请把下面内容总结成三点：这里是一段很长的业务文本");
    }

    @Test
    public void render_whenVariableMissing_shouldThrowException() {
        AiPromptTemplateService aiPromptTemplateService = Mockito.mock(AiPromptTemplateService.class);
        AiPromptRenderServiceImpl service = new AiPromptRenderServiceImpl(aiPromptTemplateService);

        AiPromptTemplate template = new AiPromptTemplate();
        template.setCode("missing.vars");
        template.setSceneCode("missing");
        template.setVersion(1);
        template.setSystemPrompt("缺少变量{{tenant_name}}");
        when(aiPromptTemplateService.findActiveTemplate("missing.vars", "missing", null, null)).thenReturn(template);

        AiChatRequest request = new AiChatRequest();
        request.setTemplateCode("missing.vars");
        request.setSceneCode("missing");

        assertThatThrownBy(() -> service.render(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenant_name");
    }
}
