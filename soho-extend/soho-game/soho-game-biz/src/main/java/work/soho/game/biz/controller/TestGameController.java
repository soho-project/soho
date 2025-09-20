package work.soho.game.biz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.game.biz.domain.GameInfo;
import work.soho.game.biz.service.GameInfoService;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/game/guest/test" )
public class TestGameController {
    private final GameInfoService gameInfoService;

    @GetMapping("/list" )
    public List<GameInfo> list() {
        return gameInfoService.testList();
    }
}
