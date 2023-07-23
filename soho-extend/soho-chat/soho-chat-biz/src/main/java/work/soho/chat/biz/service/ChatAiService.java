package work.soho.chat.biz.service;

public interface ChatAiService {
    String chat(String userInput);

    String createImage(String userInput);
}
