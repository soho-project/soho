package work.soho.game.biz.wordmatch.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.soho.game.biz.mapper.WordMatchWordMapper;
import work.soho.game.biz.wordmatch.domain.WordMatchWord;
import work.soho.game.biz.wordmatch.dto.WordDto;

import java.util.ArrayList;
import java.util.List;

@DS("game")
@RequiredArgsConstructor
@Service
public class WordLibraryServiceImpl implements WordLibraryService {
    private final WordMatchWordMapper wordMatchWordMapper;

    @Override
    public List<WordDto> listWords(String level, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        PageHelper.startPage(safePage, safeSize);
        LambdaQueryWrapper<WordMatchWord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(level)) {
            wrapper.eq(WordMatchWord::getLevel, level);
        }
        List<WordMatchWord> list = wordMatchWordMapper.selectList(wrapper);
        List<WordDto> result = new ArrayList<>();
        for (WordMatchWord item : list) {
            WordDto dto = new WordDto();
            dto.setId(item.getId());
            dto.setWord(item.getWord());
            dto.setMeaning(item.getMeaning());
            dto.setLevel(item.getLevel());
            dto.setFreq(item.getFreq());
            result.add(dto);
        }
        return result;
    }
}
