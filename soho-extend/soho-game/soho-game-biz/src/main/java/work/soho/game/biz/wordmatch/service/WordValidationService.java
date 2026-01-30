package work.soho.game.biz.wordmatch.service;

import work.soho.game.biz.wordmatch.domain.WordMatchWord;

public interface WordValidationService {
    WordMatchWord validate(String word, String level);
}
