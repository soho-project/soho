package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiProviderConfigControllerTest {

    @Test
    public void remove_deletesRelationsAndConfigs() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiProviderConfigController controller = new AiProviderConfigController(
                aiProviderConfigService,
                aiProviderModelRelService
        );

        when(aiProviderConfigService.removeByIds(Arrays.asList(1L))).thenReturn(true);
        when(aiProviderModelRelService.remove(any(LambdaQueryWrapper.class))).thenReturn(true);

        controller.remove(new Long[]{1L});

        verify(aiProviderModelRelService).remove(any(LambdaQueryWrapper.class));
        verify(aiProviderConfigService).removeByIds(Arrays.asList(1L));
    }
}
