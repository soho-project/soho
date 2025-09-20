package work.soho.game.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.game.biz.domain.GameInfo;
import work.soho.game.biz.mapper.GameInfoMapper;
import work.soho.game.biz.service.GameInfoService;

@RequiredArgsConstructor
@Service
@DS("game")
public class GameInfoServiceImpl extends ServiceImpl<GameInfoMapper, GameInfo>
        implements GameInfoService {
}
