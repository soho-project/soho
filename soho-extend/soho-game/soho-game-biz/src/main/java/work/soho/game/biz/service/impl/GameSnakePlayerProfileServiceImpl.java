package work.soho.game.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.game.biz.domain.GameSnakePlayerProfile;
import work.soho.game.biz.mapper.GameSnakePlayerProfileMapper;
import work.soho.game.biz.service.GameSnakePlayerProfileService;

@DS("game")
@RequiredArgsConstructor
@Service
public class GameSnakePlayerProfileServiceImpl extends ServiceImpl<GameSnakePlayerProfileMapper, GameSnakePlayerProfile>
    implements GameSnakePlayerProfileService{

}