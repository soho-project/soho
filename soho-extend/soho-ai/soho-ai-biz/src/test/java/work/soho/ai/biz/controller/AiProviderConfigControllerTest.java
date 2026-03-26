package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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

    @Test
    public void editByProviderUniqueId_updatesMatchedConfig() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiProviderConfigController controller = new AiProviderConfigController(
                aiProviderConfigService,
                aiProviderModelRelService
        );

        AiProviderConfig existed = new AiProviderConfig();
        existed.setId(100L);
        existed.setProviderUniqueId("provider-uid-1");
        when(aiProviderConfigService.getOne(any(LambdaQueryWrapper.class))).thenReturn(existed);
        when(aiProviderConfigService.updateById(any(AiProviderConfig.class))).thenReturn(true);

        AiProviderConfig payload = new AiProviderConfig();
        payload.setRemark("updated");

        controller.editByProviderUniqueId("provider-uid-1", payload);

        verify(aiProviderConfigService).getOne(any(LambdaQueryWrapper.class));
        verify(aiProviderConfigService).updateById(any(AiProviderConfig.class));
        verify(aiProviderModelRelService, Mockito.never()).replaceRelations(eq(100L), any());
    }

    @Test
    public void editByProviderUniqueId_createsDefaultConfigWhenMissing() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiProviderConfigController controller = new AiProviderConfigController(
                aiProviderConfigService,
                aiProviderModelRelService
        );

        when(aiProviderConfigService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(aiProviderConfigService.save(any(AiProviderConfig.class))).thenReturn(true);

        AiProviderConfig payload = new AiProviderConfig();
        payload.setApiKeyRef("override-token");
        controller.editByProviderUniqueId("provider-uid-create", payload);

        ArgumentCaptor<AiProviderConfig> captor = ArgumentCaptor.forClass(AiProviderConfig.class);
        verify(aiProviderConfigService).save(captor.capture());
        verify(aiProviderConfigService, never()).updateById(any(AiProviderConfig.class));

        AiProviderConfig saved = captor.getValue();
        org.junit.Assert.assertEquals("provider-uid-create", saved.getProviderUniqueId());
        org.junit.Assert.assertTrue(saved.getCode() != null && !saved.getCode().trim().isEmpty());
        org.junit.Assert.assertEquals("override-token", saved.getApiKeyRef());
        org.junit.Assert.assertEquals(Arrays.asList(4L, 5L, 7L, 11L, 12L, 13L, 14L), saved.getModelInfoIds());
    }

    @Test
    public void add_usesDefaultModelInfoIdsWhenMissing() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiProviderConfigController controller = new AiProviderConfigController(
                aiProviderConfigService,
                aiProviderModelRelService
        );

        when(aiProviderConfigService.save(any(AiProviderConfig.class))).thenReturn(true);

        AiProviderConfig request = new AiProviderConfig();
        controller.add(request);

        ArgumentCaptor<AiProviderConfig> captor = ArgumentCaptor.forClass(AiProviderConfig.class);
        verify(aiProviderConfigService).save(captor.capture());
        org.junit.Assert.assertEquals(Arrays.asList(4L, 5L, 7L, 11L, 12L, 13L, 14L), captor.getValue().getModelInfoIds());
    }

    @Test
    public void add_keepsIncomingModelInfoIds() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiProviderConfigController controller = new AiProviderConfigController(
                aiProviderConfigService,
                aiProviderModelRelService
        );

        when(aiProviderConfigService.save(any(AiProviderConfig.class))).thenReturn(true);

        AiProviderConfig request = new AiProviderConfig();
        request.setModelInfoIds(Collections.singletonList(99L));
        controller.add(request);

        ArgumentCaptor<AiProviderConfig> captor = ArgumentCaptor.forClass(AiProviderConfig.class);
        verify(aiProviderConfigService).save(captor.capture());
        org.junit.Assert.assertEquals(Collections.singletonList(99L), captor.getValue().getModelInfoIds());
    }
}
