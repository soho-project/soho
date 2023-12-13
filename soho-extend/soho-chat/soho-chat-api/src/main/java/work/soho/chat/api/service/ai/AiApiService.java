package work.soho.chat.api.service.ai;

import work.soho.chat.api.request.AiRequest;

import java.util.LinkedList;

public interface AiApiService {
    String query(String question);

    String chatQuery(LinkedList<AiRequest.Message> list);
}
