package work.soho.ai.biz.controller;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;
import work.soho.ai.biz.request.AiProxyBatchTestRequest;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.ai.biz.vo.AiProxyConfigMonitorVO;
import work.soho.common.core.result.R;

import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLHandshakeException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.mockito.stubbing.Answer;


import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

public class AiProxyConfigControllerTest {

    @Test
    public void getInfo_shouldReturnMonitorFields() {
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProxyRuntimeStateService aiProxyRuntimeStateService = Mockito.mock(AiProxyRuntimeStateService.class);
        AiProxyConfigController controller = new AiProxyConfigController(aiProxyConfigService, aiProxyRelayService, aiProxyRuntimeStateService);

        AiProxyConfig config = new AiProxyConfig();
        config.setId(11L);
        config.setName("proxy-11");
        config.setWeight(7);
        when(aiProxyConfigService.getById(11L)).thenReturn(config);

        AiProxyRuntimeStateSnapshot snapshot = new AiProxyRuntimeStateSnapshot();
        snapshot.setProxyConfigId(11L);
        snapshot.setEffectiveWeight(5);
        snapshot.setRequestAllowed(true);
        snapshot.setCircuitOpen(false);
        snapshot.setTotalSuccessCount(9L);
        when(aiProxyRuntimeStateService.getStateSnapshot(config)).thenReturn(snapshot);

        R<AiProxyConfigMonitorVO> response = controller.getInfo(11L);

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().getId()).isEqualTo(11L);
        assertThat(response.getPayload().getEffectiveWeight()).isEqualTo(5);
        assertThat(response.getPayload().getRequestAllowed()).isTrue();
        assertThat(response.getPayload().getTotalSuccessCount()).isEqualTo(9L);
    }

    @Test
    public void resolveBatchTestPoolSize_shouldLimitConcurrencyToFive() {
        assertEquals(0, AiProxyConfigController.resolveBatchTestPoolSize(0));
        assertEquals(1, AiProxyConfigController.resolveBatchTestPoolSize(1));
        assertEquals(5, AiProxyConfigController.resolveBatchTestPoolSize(5));
        assertEquals(5, AiProxyConfigController.resolveBatchTestPoolSize(8));
        assertEquals(5, AiProxyConfigController.getMaxBatchTestConcurrency());
    }

    @Test
    public void createBatchTestExecutor_shouldUseAtMostFiveThreads() {
        ExecutorService executor = AiProxyConfigController.createBatchTestExecutor(8);
        try {
            assertTrue(executor instanceof ThreadPoolExecutor);
            assertEquals(5, ((ThreadPoolExecutor) executor).getCorePoolSize());
            assertEquals(5, ((ThreadPoolExecutor) executor).getMaximumPoolSize());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void batchTest_shouldKeepResultOrderWhenRunningConcurrently() {
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProxyConfigController controller = Mockito.spy(new AiProxyConfigController(aiProxyConfigService, aiProxyRelayService));

        List<AiProxyConfig> configs = new ArrayList<>();
        for (long id = 1; id <= 3; id++) {
            AiProxyConfig config = new AiProxyConfig();
            config.setId(id);
            config.setName("proxy-" + id);
            configs.add(config);
        }
        when(aiProxyConfigService.listByIds(List.of(1L, 2L, 3L))).thenReturn(configs);
        Mockito.doAnswer((Answer<AiProxyConfigController.AiProxyNodeTestResult>) invocation -> {
            AiProxyConfig item = invocation.getArgument(0);
            if (item.getId() == 1L) {
                Thread.sleep(150L);
            } else if (item.getId() == 2L) {
                Thread.sleep(30L);
            } else {
                Thread.sleep(80L);
            }
            AiProxyConfigController.AiProxyNodeTestResult result = new AiProxyConfigController.AiProxyNodeTestResult();
            result.setId(item.getId());
            result.setName(item.getName());
            result.setSuccess(true);
            result.setMessage("ok");
            return result;
        }).when(controller).testSingleProxy(Mockito.any(AiProxyConfig.class), Mockito.anyString(), Mockito.anyInt());

        AiProxyBatchTestRequest request = new AiProxyBatchTestRequest();
        request.setIds(List.of(1L, 2L, 3L));
        request.setTestUrl("https://chatgpt.com");
        request.setTimeoutMs(1000);

        R<AiProxyConfigController.AiProxyBatchTestResponse> response = controller.batchTest(request);

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().getResults())
                .extracting(AiProxyConfigController.AiProxyNodeTestResult::getId)
                .containsExactly(1L, 2L, 3L);
        assertThat(response.getPayload().getSuccessCount()).isEqualTo(3);
        assertThat(response.getPayload().getFailedCount()).isEqualTo(0);
    }

    @Test
    public void batchTest_shouldReturnLatencyAndResolvedRelayInfo() throws Exception {
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProxyConfigController controller = new AiProxyConfigController(aiProxyConfigService, aiProxyRelayService);

        try (ServerSocket tunnelServer = new ServerSocket(0)) {
            CountDownLatch accepted = new CountDownLatch(1);
            AtomicReference<String> requestLineRef = new AtomicReference<>();
            Thread serverThread = new Thread(() -> serveSimpleGetProxy(tunnelServer, accepted, requestLineRef));
            serverThread.setDaemon(true);
            serverThread.start();

            AiProxyConfig config = new AiProxyConfig();
            config.setId(1L);
            config.setName("proxy-1");
            config.setProvider("openai");
            config.setProxyType("http");
            config.setProxyHost("127.0.0.1");
            config.setProxyPort(tunnelServer.getLocalPort());
            when(aiProxyConfigService.listByIds(List.of(1L))).thenReturn(List.of(config));
            when(aiProxyRelayService.ensureRelay(Mockito.any(), Mockito.eq("openai")))
                    .thenAnswer(invocation -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("proxyType", "http");
                        map.put("proxyHost", "127.0.0.1");
                        map.put("proxyPort", tunnelServer.getLocalPort());
                        return AiProxyLayerUtils.resolve(map);
                    });

            AiProxyBatchTestRequest request = new AiProxyBatchTestRequest();
            request.setIds(List.of(1L));
            request.setTestUrl("http://example.com/health");
            request.setTimeoutMs(1000);

            R<AiProxyConfigController.AiProxyBatchTestResponse> response = controller.batchTest(request);
            assertThat(accepted.await(2, TimeUnit.SECONDS)).isTrue();

            assertThat(response.getPayload()).isNotNull();
            assertThat(requestLineRef.get()).startsWith("GET ");
            assertThat(response.getPayload().getResults()).hasSize(1);
            AiProxyConfigController.AiProxyNodeTestResult result = response.getPayload().getResults().get(0);
            assertThat(result.getTunnelLatencyMs()).isNotNull();
            assertThat(result.getResolvedProxyType()).isEqualTo("http");
            assertThat(result.getResolvedHost()).isEqualTo("127.0.0.1");
            assertThat(result.getResolvedPort()).isEqualTo(tunnelServer.getLocalPort());
            assertThat(result.getRelayMode()).isEqualTo("directProxy");
            assertThat(result.getMessage()).isNotBlank();
            assertThat(result.getSuccess()).isNotNull();
            if (Boolean.TRUE.equals(result.getSuccess())) {
                assertThat(result.getTargetLatencyMs()).isNotNull();
                assertThat(result.getStatusCode()).isEqualTo(204);
                assertThat(result.getMessage()).isEqualTo("ok");
            }
        }
    }

    private void serveSimpleGetProxy(ServerSocket serverSocket, CountDownLatch accepted, AtomicReference<String> requestLineRef) {
        try {
            while (requestLineRef.get() == null) {
                try (Socket socket = serverSocket.accept()) {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int total = 0;
                    while (total < buffer.length) {
                        int read = inputStream.read(buffer, total, buffer.length - total);
                        if (read < 0) {
                            break;
                        }
                        total += read;
                        String request = new String(buffer, 0, total);
                        if (request.contains("\r\n\r\n")) {
                            String[] lines = request.split("\\r\\n", 2);
                            requestLineRef.set(lines.length > 0 ? lines[0] : null);
                            accepted.countDown();
                            outputStream.write(("HTTP/1.1 204 No Content\r\n"
                                    + "Content-Length: 0\r\n"
                                    + "Connection: close\r\n\r\n").getBytes());
                            outputStream.flush();
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    public void batchTest_shouldReturnTlsHostnameFailureMessage() throws Exception {
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProxyConfigController controller = Mockito.spy(new AiProxyConfigController(aiProxyConfigService, aiProxyRelayService));

        AiProxyConfig config = new AiProxyConfig();
        config.setId(1L);
        config.setName("proxy-1");
        config.setProvider("openai");
        config.setProxyType("socks5");
        config.setProxyHost("127.0.0.1");
        config.setProxyPort(1080);
        when(aiProxyConfigService.listByIds(List.of(1L))).thenReturn(List.of(config));
        when(aiProxyRelayService.ensureRelay(Mockito.any(), Mockito.eq("openai"))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.doReturn(1L).when(controller).checkProxyTunnel(Mockito.any(), Mockito.anyInt());
        Mockito.doThrow(new SSLHandshakeException("No subject alternative DNS name matching chatgpt.com found."))
                .when(controller).executeTargetProbe(Mockito.any(), Mockito.any(), Mockito.anyInt());

        AiProxyBatchTestRequest request = new AiProxyBatchTestRequest();
        request.setIds(List.of(1L));
        request.setTestUrl("https://chatgpt.com");
        request.setTimeoutMs(1000);

        R<AiProxyConfigController.AiProxyBatchTestResponse> response = controller.batchTest(request);

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().getResults()).hasSize(1);
        AiProxyConfigController.AiProxyNodeTestResult result = response.getPayload().getResults().get(0);
        assertThat(result.getSuccess()).isFalse();
        assertThat(result.getMessage()).contains("tls hostname verification failed via proxy for https://chatgpt.com");
    }
}
