package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.UserAiChatRequest;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiChatSessionMessageService;
import work.soho.ai.biz.service.AiChatSessionService;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiPromptRenderLogService;
import work.soho.ai.biz.service.AiPromptRenderService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.service.WalletInfoService;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiUserWebChatServiceImplTest {

    @Test
    public void prepareSession_whenExistingSessionHasProviderCode_shouldClearBinding() throws Exception {
        AiChatSessionService aiChatSessionService = Mockito.mock(AiChatSessionService.class);
        AiUserWebChatServiceImpl service = buildService(aiChatSessionService);

        AiChatSession session = new AiChatSession();
        session.setId(10L);
        session.setUserId(99L);
        session.setProviderCode("fixed-provider");
        session.setModel("gpt-4o-mini");
        when(aiChatSessionService.requireOwnedSession(99L, 10L)).thenReturn(session);

        UserAiChatRequest request = new UserAiChatRequest();
        request.setSessionId(10L);
        request.setModel("gpt-4o-mini");

        Method method = AiUserWebChatServiceImpl.class.getDeclaredMethod("prepareSession", Long.class, UserAiChatRequest.class);
        method.setAccessible(true);
        AiChatSession prepared = (AiChatSession) method.invoke(service, 99L, request);

        assertThat(prepared.getProviderCode()).isNull();
        verify(aiChatSessionService).updateById(prepared);
    }

    @Test
    public void toAiChatRequest_whenSessionHasProviderCode_shouldUseRequestProviderCodeOnly() throws Exception {
        AiUserWebChatServiceImpl service = buildService(Mockito.mock(AiChatSessionService.class));

        AiChatSession session = new AiChatSession();
        session.setProviderCode("session-provider");
        session.setModel("gpt-4o-mini");

        UserAiChatRequest request = new UserAiChatRequest();
        request.setProviderCode("request-provider");
        request.setModel("gpt-4.1");

        Method method = AiUserWebChatServiceImpl.class.getDeclaredMethod("toAiChatRequest", UserAiChatRequest.class, AiChatSession.class);
        method.setAccessible(true);
        AiChatRequest aiChatRequest = (AiChatRequest) method.invoke(service, request, session);

        assertThat(aiChatRequest.getProviderCode()).isEqualTo("request-provider");
        assertThat(aiChatRequest.getModel()).isEqualTo("gpt-4.1");
    }

    @Test
    public void listSessions_whenSessionHasProviderCode_shouldHideBindingFromClient() {
        AiChatSessionService aiChatSessionService = Mockito.mock(AiChatSessionService.class);
        AiUserWebChatServiceImpl service = buildService(aiChatSessionService);

        AiChatSession session = new AiChatSession();
        session.setId(1L);
        session.setProviderCode("fixed-provider");
        when(aiChatSessionService.list(any())).thenReturn(Collections.singletonList(session));

        List<AiChatSession> sessions = service.listSessions(99L);

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getProviderCode()).isNull();
    }

    private AiUserWebChatServiceImpl buildService(AiChatSessionService aiChatSessionService) {
        return new AiUserWebChatServiceImpl(
                Mockito.mock(AiProviderConfigService.class),
                Mockito.mock(AiProviderModelRelService.class),
                Mockito.mock(AiChatService.class),
                Mockito.mock(AiApiCallLogService.class),
                aiChatSessionService,
                Mockito.mock(AiChatSessionMessageService.class),
                Mockito.mock(WalletInfoService.class),
                Mockito.mock(WalletInfoApiService.class),
                Mockito.mock(AiMemberRequestLimitService.class),
                Mockito.mock(AiUserMemberCardService.class),
                Mockito.mock(AiPromptRenderService.class),
                Mockito.mock(AiPromptRenderLogService.class)
        );
    }
}
