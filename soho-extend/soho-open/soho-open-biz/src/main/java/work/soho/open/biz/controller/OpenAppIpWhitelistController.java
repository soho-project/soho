package work.soho.open.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
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
import work.soho.open.biz.domain.OpenAppIpWhitelist;
import work.soho.open.biz.service.OpenAppIpWhitelistService;
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
@Api(tags = "")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openAppIpWhitelist" )
public class OpenAppIpWhitelistController {

    private final OpenAppIpWhitelistService openAppIpWhitelistService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openAppIpWhitelist::list", name = "获取  列表")
    public R<PageSerializable<OpenAppIpWhitelist>> list(OpenAppIpWhitelist openAppIpWhitelist, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAppIpWhitelist> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openAppIpWhitelist.getId() != null, OpenAppIpWhitelist::getId ,openAppIpWhitelist.getId());
        lqw.eq(openAppIpWhitelist.getAppId() != null, OpenAppIpWhitelist::getAppId ,openAppIpWhitelist.getAppId());
        lqw.like(StringUtils.isNotBlank(openAppIpWhitelist.getIp()),OpenAppIpWhitelist::getIp ,openAppIpWhitelist.getIp());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenAppIpWhitelist::getId);
        List<OpenAppIpWhitelist> list = openAppIpWhitelistService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openAppIpWhitelist::getInfo", name = "获取  详细信息")
    public R<OpenAppIpWhitelist> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppIpWhitelistService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openAppIpWhitelist::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenAppIpWhitelist openAppIpWhitelist) {
        return R.success(openAppIpWhitelistService.save(openAppIpWhitelist));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openAppIpWhitelist::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenAppIpWhitelist openAppIpWhitelist) {
        return R.success(openAppIpWhitelistService.updateById(openAppIpWhitelist));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openAppIpWhitelist::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppIpWhitelistService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenAppIpWhitelist.class)
    @Node(value = "openAppIpWhitelist::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenAppIpWhitelist openAppIpWhitelist, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenAppIpWhitelist> lqw = new LambdaQueryWrapper<OpenAppIpWhitelist>();
        lqw.eq(openAppIpWhitelist.getId() != null, OpenAppIpWhitelist::getId ,openAppIpWhitelist.getId());
        lqw.eq(openAppIpWhitelist.getAppId() != null, OpenAppIpWhitelist::getAppId ,openAppIpWhitelist.getAppId());
        lqw.like(StringUtils.isNotBlank(openAppIpWhitelist.getIp()),OpenAppIpWhitelist::getIp ,openAppIpWhitelist.getIp());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenAppIpWhitelist::getId);
        return openAppIpWhitelistService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openAppIpWhitelist::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenAppIpWhitelist.class, new ReadListener<OpenAppIpWhitelist>() {
                @Override
                public void invoke(OpenAppIpWhitelist openAppIpWhitelist, AnalysisContext analysisContext) {
                    openAppIpWhitelistService.save(openAppIpWhitelist);
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