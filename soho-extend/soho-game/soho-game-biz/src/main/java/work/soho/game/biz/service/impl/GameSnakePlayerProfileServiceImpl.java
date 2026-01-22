package work.soho.game.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.game.biz.domain.GameSnakePlayerProfile;
import work.soho.game.biz.mapper.GameSnakePlayerProfileMapper;
import work.soho.game.biz.service.GameSnakePlayerProfileService;

@RequiredArgsConstructor
@Service
public class GameSnakePlayerProfileServiceImpl extends ServiceImpl<GameSnakePlayerProfileMapper, GameSnakePlayerProfile>
    implements GameSnakePlayerProfileService{

}