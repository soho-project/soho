package work.soho.game.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.game.biz.domain.GameSnakePlayerProfile;
import work.soho.game.biz.service.GameSnakePlayerProfileService;

import java.util.Arrays;
import java.util.List;
/**
 * Controller
 *
 * @author fang
 */
@Api(value="",tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/game/admin/gameSnakePlayerProfile" )
public class GameSnakePlayerProfileController {

    private final GameSnakePlayerProfileService gameSnakePlayerProfileService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "gameSnakePlayerProfile::list", name = "获取  列表")
    @ApiOperation(value = "获取  列表", notes = "获取  列表")
    public R<PageSerializable<GameSnakePlayerProfile>> list(GameSnakePlayerProfile gameSnakePlayerProfile, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<GameSnakePlayerProfile> lqw = new LambdaQueryWrapper<>();
        lqw.eq(gameSnakePlayerProfile.getId() != null, GameSnakePlayerProfile::getId ,gameSnakePlayerProfile.getId());
        lqw.eq(gameSnakePlayerProfile.getUserId() != null, GameSnakePlayerProfile::getUserId, gameSnakePlayerProfile.getUserId());
        lqw.eq(gameSnakePlayerProfile.getReviveCards() != null, GameSnakePlayerProfile::getReviveCards ,gameSnakePlayerProfile.getReviveCards());
        lqw.eq(gameSnakePlayerProfile.getUpdatedTime() != null, GameSnakePlayerProfile::getUpdatedTime ,gameSnakePlayerProfile.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GameSnakePlayerProfile::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GameSnakePlayerProfile::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(GameSnakePlayerProfile::getId);
        List<GameSnakePlayerProfile> list = gameSnakePlayerProfileService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "gameSnakePlayerProfile::getInfo", name = "获取  详细信息")
    @ApiOperation(value = "获取  详细信息", notes = "获取  详细信息")
    public R<GameSnakePlayerProfile> getInfo(@PathVariable("id" ) Long id) {
        return R.success(gameSnakePlayerProfileService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "gameSnakePlayerProfile::add", name = "新增 ")
    @ApiOperation(value = "新增 ", notes = "新增 ")
    public R<Boolean> add(@RequestBody GameSnakePlayerProfile gameSnakePlayerProfile) {
        return R.success(gameSnakePlayerProfileService.save(gameSnakePlayerProfile));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "gameSnakePlayerProfile::edit", name = "修改 ")
    @ApiOperation(value = "修改 ", notes = "修改 ")
    public R<Boolean> edit(@RequestBody GameSnakePlayerProfile gameSnakePlayerProfile) {
        return R.success(gameSnakePlayerProfileService.updateById(gameSnakePlayerProfile));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "gameSnakePlayerProfile::remove", name = "删除 ")
    @ApiOperation(value = "删除 ", notes = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(gameSnakePlayerProfileService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = GameSnakePlayerProfile.class)
    @Node(value = "gameSnakePlayerProfile::exportExcel", name = "导出  Excel")
    @ApiOperation(value = "导出  Excel", notes = "导出  Excel")
    public Object exportExcel(GameSnakePlayerProfile gameSnakePlayerProfile, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<GameSnakePlayerProfile> lqw = new LambdaQueryWrapper<GameSnakePlayerProfile>();
        lqw.eq(gameSnakePlayerProfile.getId() != null, GameSnakePlayerProfile::getId ,gameSnakePlayerProfile.getId());
        lqw.eq(gameSnakePlayerProfile.getUserId() != null, GameSnakePlayerProfile::getUserId, gameSnakePlayerProfile.getUserId());
        lqw.eq(gameSnakePlayerProfile.getReviveCards() != null, GameSnakePlayerProfile::getReviveCards ,gameSnakePlayerProfile.getReviveCards());
        lqw.eq(gameSnakePlayerProfile.getUpdatedTime() != null, GameSnakePlayerProfile::getUpdatedTime ,gameSnakePlayerProfile.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GameSnakePlayerProfile::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GameSnakePlayerProfile::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(GameSnakePlayerProfile::getId);
        return gameSnakePlayerProfileService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "gameSnakePlayerProfile::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入  Excel", notes = "导入  Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), GameSnakePlayerProfile.class, new ReadListener<GameSnakePlayerProfile>() {
                @Override
                public void invoke(GameSnakePlayerProfile gameSnakePlayerProfile, AnalysisContext analysisContext) {
                    gameSnakePlayerProfileService.save(gameSnakePlayerProfile);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}
