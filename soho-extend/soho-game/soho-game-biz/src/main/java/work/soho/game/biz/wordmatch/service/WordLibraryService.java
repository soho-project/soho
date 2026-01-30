package work.soho.game.biz.wordmatch.service;

import work.soho.game.biz.wordmatch.dto.WordDto;

import java.util.List;

public interface WordLibraryService {
    List<WordDto> listWords(String level, int page, int size);
}
