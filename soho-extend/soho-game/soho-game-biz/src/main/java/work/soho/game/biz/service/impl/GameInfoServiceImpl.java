package work.soho.game.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.game.biz.domain.GameInfo;
import work.soho.game.biz.mapper.GameInfoMapper;
import work.soho.game.biz.service.GameInfoService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@DS("game")
public class GameInfoServiceImpl extends ServiceImpl<GameInfoMapper, GameInfo>
        implements GameInfoService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<GameInfo> testList() {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setTitle("测试");
        gameInfo.setName("测试");
        gameInfo.setUpdatedTime(LocalDateTime.now());
        gameInfo.setCreatedTime(LocalDateTime.now());
        save(gameInfo);

        if( true) {
//            throw new RuntimeException("测试事务");
        }

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setTitle("测试2");
        gameInfo2.setName("测试2");
        gameInfo2.setUpdatedTime(LocalDateTime.now());
        gameInfo2.setCreatedTime(LocalDateTime.now());
        save(gameInfo2);
        return list();
    }
}
