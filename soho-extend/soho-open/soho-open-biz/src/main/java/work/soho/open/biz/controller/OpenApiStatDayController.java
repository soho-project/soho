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
import work.soho.open.biz.domain.OpenApiStatDay;
import work.soho.open.biz.service.OpenApiStatDayService;
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
@RequestMapping("/open/admin/openApiStatDay" )
public class OpenApiStatDayController {

    private final OpenApiStatDayService openApiStatDayService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openApiStatDay::list", name = "获取  列表")
    public R<PageSerializable<OpenApiStatDay>> list(OpenApiStatDay openApiStatDay, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApiStatDay> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openApiStatDay.getId() != null, OpenApiStatDay::getId ,openApiStatDay.getId());
        lqw.eq(openApiStatDay.getAppId() != null, OpenApiStatDay::getAppId ,openApiStatDay.getAppId());
        lqw.eq(openApiStatDay.getApiId() != null, OpenApiStatDay::getApiId ,openApiStatDay.getApiId());
        lqw.eq(openApiStatDay.getStatDate() != null, OpenApiStatDay::getStatDate ,openApiStatDay.getStatDate());
        lqw.eq(openApiStatDay.getCallCount() != null, OpenApiStatDay::getCallCount ,openApiStatDay.getCallCount());
        lqw.eq(openApiStatDay.getFailCount() != null, OpenApiStatDay::getFailCount ,openApiStatDay.getFailCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApiStatDay::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApiStatDay::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenApiStatDay::getId);
        List<OpenApiStatDay> list = openApiStatDayService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openApiStatDay::getInfo", name = "获取  详细信息")
    public R<OpenApiStatDay> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openApiStatDayService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openApiStatDay::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenApiStatDay openApiStatDay) {
        return R.success(openApiStatDayService.save(openApiStatDay));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openApiStatDay::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenApiStatDay openApiStatDay) {
        return R.success(openApiStatDayService.updateById(openApiStatDay));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openApiStatDay::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openApiStatDayService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenApiStatDay.class)
    @Node(value = "openApiStatDay::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenApiStatDay openApiStatDay, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenApiStatDay> lqw = new LambdaQueryWrapper<OpenApiStatDay>();
        lqw.eq(openApiStatDay.getId() != null, OpenApiStatDay::getId ,openApiStatDay.getId());
        lqw.eq(openApiStatDay.getAppId() != null, OpenApiStatDay::getAppId ,openApiStatDay.getAppId());
        lqw.eq(openApiStatDay.getApiId() != null, OpenApiStatDay::getApiId ,openApiStatDay.getApiId());
        lqw.eq(openApiStatDay.getStatDate() != null, OpenApiStatDay::getStatDate ,openApiStatDay.getStatDate());
        lqw.eq(openApiStatDay.getCallCount() != null, OpenApiStatDay::getCallCount ,openApiStatDay.getCallCount());
        lqw.eq(openApiStatDay.getFailCount() != null, OpenApiStatDay::getFailCount ,openApiStatDay.getFailCount());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApiStatDay::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApiStatDay::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenApiStatDay::getId);
        return openApiStatDayService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openApiStatDay::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenApiStatDay.class, new ReadListener<OpenApiStatDay>() {
                @Override
                public void invoke(OpenApiStatDay openApiStatDay, AnalysisContext analysisContext) {
                    openApiStatDayService.save(openApiStatDay);
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