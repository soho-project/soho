package work.soho.game.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.game.biz.domain.GameInfo;
import work.soho.game.biz.service.GameInfoService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * Controller
 *
 * @author fang
 */
@Api(value="",tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/game/admin/gameInfo" )
public class GameInfoController {

    private final GameInfoService gameInfoService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "gameInfo::list", name = "获取  列表")
    @ApiOperation(value = "获取  列表", notes = "获取  列表")
    public R<PageSerializable<GameInfo>> list(GameInfo gameInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<GameInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(gameInfo.getId() != null, GameInfo::getId ,gameInfo.getId());
        lqw.like(StringUtils.isNotBlank(gameInfo.getName()),GameInfo::getName ,gameInfo.getName());
        lqw.like(StringUtils.isNotBlank(gameInfo.getTitle()),GameInfo::getTitle ,gameInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(gameInfo.getLogo()),GameInfo::getLogo ,gameInfo.getLogo());
        lqw.eq(gameInfo.getUpdatedTime() != null, GameInfo::getUpdatedTime ,gameInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GameInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GameInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(GameInfo::getId);
        List<GameInfo> list = gameInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "gameInfo::getInfo", name = "获取  详细信息")
    @ApiOperation(value = "获取  详细信息", notes = "获取  详细信息")
    public R<GameInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(gameInfoService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "gameInfo::add", name = "新增 ")
    @ApiOperation(value = "新增 ", notes = "新增 ")
    public R<Boolean> add(@RequestBody GameInfo gameInfo) {
        return R.success(gameInfoService.save(gameInfo));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "gameInfo::edit", name = "修改 ")
    @ApiOperation(value = "修改 ", notes = "修改 ")
    public R<Boolean> edit(@RequestBody GameInfo gameInfo) {
        return R.success(gameInfoService.updateById(gameInfo));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "gameInfo::remove", name = "删除 ")
    @ApiOperation(value = "删除 ", notes = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(gameInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "gameInfo::options", name = "获取  选项")
    @ApiOperation(value = "获取  选项", notes = "获取  选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<GameInfo> list = gameInfoService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(GameInfo item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getTitle());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = GameInfo.class)
    @Node(value = "gameInfo::exportExcel", name = "导出  Excel")
    @ApiOperation(value = "导出  Excel", notes = "导出  Excel")
    public Object exportExcel(GameInfo gameInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<GameInfo> lqw = new LambdaQueryWrapper<GameInfo>();
        lqw.eq(gameInfo.getId() != null, GameInfo::getId ,gameInfo.getId());
        lqw.like(StringUtils.isNotBlank(gameInfo.getName()),GameInfo::getName ,gameInfo.getName());
        lqw.like(StringUtils.isNotBlank(gameInfo.getTitle()),GameInfo::getTitle ,gameInfo.getTitle());
        lqw.like(StringUtils.isNotBlank(gameInfo.getLogo()),GameInfo::getLogo ,gameInfo.getLogo());
        lqw.eq(gameInfo.getUpdatedTime() != null, GameInfo::getUpdatedTime ,gameInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GameInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GameInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(GameInfo::getId);
        return gameInfoService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "gameInfo::importExcel", name = "导入 自动化样例 Excel")
    @ApiOperation(value = "导入  Excel", notes = "导入  Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), GameInfo.class, new ReadListener<GameInfo>() {
                @Override
                public void invoke(GameInfo gameInfo, AnalysisContext analysisContext) {
                    gameInfoService.save(gameInfo);
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