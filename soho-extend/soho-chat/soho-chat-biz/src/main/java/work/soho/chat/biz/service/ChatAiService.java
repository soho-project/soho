package work.soho.chat.biz.service;

public interface ChatAiService {
    /**
     * 聊天问答
     *
     * @param userInput
     * @return
     */
    String chat(String userInput);

    /**
     * 根据文本创建图片
     *
     * @param userInput
     * @return
     */
    String createImage(String userInput);

    /**
     * 语音文件转文本
     *
     * @param url
     * @return
     */
    String audio2Text(String url);
}
