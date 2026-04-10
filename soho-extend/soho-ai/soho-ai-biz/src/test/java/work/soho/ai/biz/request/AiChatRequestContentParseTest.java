package work.soho.ai.biz.request;

import org.junit.Test;
import work.soho.common.core.util.JacksonUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class AiChatRequestContentParseTest {

    @Test
    public void shouldParseInputImageAndInputFileContentBlocks() {
        String raw = "{"
                + "\"messages\":[{"
                + "\"role\":\"user\","
                + "\"content\":["
                + "{\"type\":\"input_text\",\"text\":\"请分析附件\"},"
                + "{\"type\":\"input_image\",\"image_url\":{\"url\":\"https://example.com/a.png\"}},"
                + "{\"type\":\"input_file\",\"file_url\":{\"url\":\"https://example.com/a.pdf\"}}"
                + "]"
                + "}]"
                + "}";

        AiChatRequest request = JacksonUtils.toBean(raw, AiChatRequest.class);
        AiChatRequest.Message message = request.getMessages().get(0);

        assertThat(message.getContent()).isEqualTo("请分析附件");
        assertThat(message.getImageUrls()).containsExactly("https://example.com/a.png");
        assertThat(message.getFileUrls()).containsExactly("https://example.com/a.pdf");
    }

    @Test
    public void shouldParseSimpleImageAndFileBlocks() {
        String raw = "{"
                + "\"messages\":[{"
                + "\"role\":\"user\","
                + "\"content\":["
                + "{\"type\":\"text\",\"text\":\"看下图片和文件\"},"
                + "{\"type\":\"image\",\"url\":\"https://example.com/b.png\"},"
                + "{\"type\":\"file\",\"url\":\"https://example.com/b.txt\"}"
                + "]"
                + "}]"
                + "}";

        AiChatRequest request = JacksonUtils.toBean(raw, AiChatRequest.class);
        AiChatRequest.Message message = request.getMessages().get(0);

        assertThat(message.getContent()).isEqualTo("看下图片和文件");
        assertThat(message.getImageUrls()).containsExactly("https://example.com/b.png");
        assertThat(message.getFileUrls()).containsExactly("https://example.com/b.txt");
    }
}
