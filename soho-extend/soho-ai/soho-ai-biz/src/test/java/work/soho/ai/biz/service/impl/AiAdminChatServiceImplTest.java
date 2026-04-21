package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiChatSessionMessageService;
import work.soho.ai.biz.service.AiChatSessionService;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiModelRouteService;
import work.soho.ai.biz.service.AiPromptRenderLogService;
import work.soho.ai.biz.service.AiPromptRenderService;
import work.soho.ai.biz.service.AiProviderConfigService;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiAdminChatServiceImplTest {

    @Test
    public void prepareSession_whenExistingSessionHasProviderCode_shouldClearBinding() throws Exception {
        AiChatSessionService aiChatSessionService = Mockito.mock(AiChatSessionService.class);
        AiAdminChatServiceImpl service = buildService(aiChatSessionService);

        AiChatSession session = new AiChatSession();
        session.setId(12L);
        session.setUserId(-7L);
        session.setProviderCode("fixed-provider");
        session.setModel("gpt-4o-mini");
        when(aiChatSessionService.requireSessionByOwnerId(-7L, 12L)).thenReturn(session);

        UserAiChatRequest request = new UserAiChatRequest();
        request.setSessionId(12L);
        request.setModel("gpt-4o-mini");

        Method method = AiAdminChatServiceImpl.class.getDeclaredMethod("prepareSession", Long.class, UserAiChatRequest.class);
        method.setAccessible(true);
        AiChatSession prepared = (AiChatSession) method.invoke(service, 7L, request);

        assertThat(prepared.getProviderCode()).isNull();
        verify(aiChatSessionService).updateById(prepared);
    }

    @Test
    public void toAiChatRequest_whenSessionHasProviderCode_shouldUseRequestProviderCodeOnly() throws Exception {
        AiAdminChatServiceImpl service = buildService(Mockito.mock(AiChatSessionService.class));

        AiChatSession session = new AiChatSession();
        session.setProviderCode("session-provider");
        session.setModel("gpt-4o-mini");

        UserAiChatRequest request = new UserAiChatRequest();
        request.setProviderCode("request-provider");
        request.setModel("gpt-4.1");

        Method method = AiAdminChatServiceImpl.class.getDeclaredMethod("toAiChatRequest", UserAiChatRequest.class, AiChatSession.class);
        method.setAccessible(true);
        AiChatRequest aiChatRequest = (AiChatRequest) method.invoke(service, request, session);

        assertThat(aiChatRequest.getProviderCode()).isEqualTo("request-provider");
        assertThat(aiChatRequest.getModel()).isEqualTo("gpt-4.1");
    }

    @Test
    public void listSessions_whenSessionHasProviderCode_shouldHideBindingFromClient() {
        AiChatSessionService aiChatSessionService = Mockito.mock(AiChatSessionService.class);
        AiAdminChatServiceImpl service = buildService(aiChatSessionService);

        AiChatSession session = new AiChatSession();
        session.setId(2L);
        session.setProviderCode("fixed-provider");
        when(aiChatSessionService.list(any())).thenReturn(Collections.singletonList(session));

        List<AiChatSession> sessions = service.listSessions(7L);

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getProviderCode()).isNull();
    }

    private AiAdminChatServiceImpl buildService(AiChatSessionService aiChatSessionService) {
        return new AiAdminChatServiceImpl(
                Mockito.mock(AiProviderConfigService.class),
                Mockito.mock(AiModelRouteService.class),
                Mockito.mock(AiChatService.class),
                aiChatSessionService,
                Mockito.mock(AiChatSessionMessageService.class),
                Mockito.mock(AiPromptRenderService.class),
                Mockito.mock(AiPromptRenderLogService.class),
                Mockito.mock(AiFileService.class)
        );
    }
}
