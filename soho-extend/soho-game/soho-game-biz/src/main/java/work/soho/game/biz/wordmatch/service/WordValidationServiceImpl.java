package work.soho.game.biz.wordmatch.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.soho.game.biz.mapper.WordMatchWordMapper;
import work.soho.game.biz.wordmatch.domain.WordMatchWord;

import java.util.List;

@DS("game")
@RequiredArgsConstructor
@Service
public class WordValidationServiceImpl implements WordValidationService {
    private final WordMatchWordMapper wordMatchWordMapper;

    @Override
    public WordMatchWord validate(String word, String level) {
        if (!StringUtils.hasText(word)) {
            return null;
        }
        LambdaQueryWrapper<WordMatchWord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WordMatchWord::getWord, word.trim());
        if (StringUtils.hasText(level)) {
            wrapper.eq(WordMatchWord::getLevel, level.trim());
        }
        wrapper.orderByDesc(WordMatchWord::getId);
        List<WordMatchWord> list = wordMatchWordMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }
}
