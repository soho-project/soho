package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class AiProxyConfigServiceImplTest {

    @Test
    public void selectProxyByProvider_whenProviderBoundExists_shouldUseProviderBoundFirst() {
        AiProxyConfigServiceImpl service = Mockito.spy(new AiProxyConfigServiceImpl());
        AiProxyConfig bound = new AiProxyConfig();
        bound.setId(10L);
        bound.setProvider("openai");
        bound.setProxyType("http");
        bound.setProxyHost("127.0.0.1");
        bound.setProxyPort(8080);
        bound.setWeight(100);
        doReturn(List.of(bound)).when(service).list(any(LambdaQueryWrapper.class));

        Optional<AiProxyConfig> selected = service.selectProxyByProvider("openai");

        assertThat(selected).isPresent();
        assertThat(selected.get().getId()).isEqualTo(10L);
    }

    @Test
    public void selectProxyByProvider_whenProviderBoundMissing_shouldFallbackToGlobal() {
        AiProxyConfigServiceImpl service = Mockito.spy(new AiProxyConfigServiceImpl());
        AiProxyConfig global = new AiProxyConfig();
        global.setId(22L);
        global.setProvider("");
        global.setProxyType("socks5");
        global.setProxyHost("127.0.0.1");
        global.setProxyPort(7890);
        global.setWeight(50);
        doReturn(Collections.emptyList(), List.of(global)).when(service).list(any(LambdaQueryWrapper.class));

        Optional<AiProxyConfig> selected = service.selectProxyByProvider("gemini");

        assertThat(selected).isPresent();
        assertThat(selected.get().getId()).isEqualTo(22L);
    }

    @Test
    public void resolveProxySettings_shouldConvertSelectedEntityToProxySettings() {
        AiProxyConfigServiceImpl service = Mockito.spy(new AiProxyConfigServiceImpl());
        AiProxyConfig bound = new AiProxyConfig();
        bound.setProvider("openai");
        bound.setProxyType("ss");
        bound.setProxyHost("127.0.0.1");
        bound.setProxyPort(7890);
        bound.setWeight(1);
        doReturn(Optional.of(bound)).when(service).selectProxyByProvider("openai");

        AiProxyLayerUtils.ProxySettings settings = service.resolveProxySettings("openai");

        assertThat(settings).isNotNull();
        assertThat(settings.getHost()).isEqualTo("127.0.0.1");
        assertThat(settings.getPort()).isEqualTo(7890);
        assertThat(settings.isLocalRelayRequired()).isTrue();
    }
}
