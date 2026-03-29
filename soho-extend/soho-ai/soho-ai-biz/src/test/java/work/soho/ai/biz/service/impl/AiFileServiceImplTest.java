package work.soho.ai.biz.service.impl;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AiFileServiceImplTest {

    @Test
    public void isBlockedFileUrl_shouldBlockLocalAndPrivateTargets() {
        AiFileServiceImpl service = new AiFileServiceImpl();

        assertThat(service.isBlockedFileUrl("http://localhost/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://127.0.0.1/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://192.168.1.10/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("http://10.0.0.8/test.txt")).isTrue();
        assertThat(service.isBlockedFileUrl("ftp://example.com/test.txt")).isTrue();
    }

    @Test
    public void isBlockedFileUrl_shouldAllowPublicHttpHost() {
        AiFileServiceImpl service = new AiFileServiceImpl();
        assertThat(service.isBlockedFileUrl("https://8.8.8.8/test.txt")).isFalse();
    }

    @Test
    public void extractTextFromUrl_whenBlockedUrl_shouldReturnEmpty() {
        AiFileServiceImpl service = new AiFileServiceImpl();
        assertThat(service.extractTextFromUrl("http://127.0.0.1/private.txt")).isEmpty();
    }
}
