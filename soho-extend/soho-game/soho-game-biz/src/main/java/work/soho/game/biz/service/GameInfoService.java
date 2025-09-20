package work.soho.game.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.game.biz.domain.GameInfo;

import java.util.List;

public interface GameInfoService extends IService<GameInfo> {
    List<GameInfo> testList();
}
