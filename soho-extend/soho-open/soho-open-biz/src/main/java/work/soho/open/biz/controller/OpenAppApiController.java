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
import work.soho.open.biz.domain.OpenAppApi;
import work.soho.open.biz.service.OpenAppApiService;
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
@RequestMapping("/open/admin/openAppApi" )
public class OpenAppApiController {

    private final OpenAppApiService openAppApiService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openAppApi::list", name = "获取  列表")
    public R<PageSerializable<OpenAppApi>> list(OpenAppApi openAppApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAppApi> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openAppApi.getId() != null, OpenAppApi::getId ,openAppApi.getId());
        lqw.eq(openAppApi.getAppId() != null, OpenAppApi::getAppId ,openAppApi.getAppId());
        lqw.eq(openAppApi.getApiId() != null, OpenAppApi::getApiId ,openAppApi.getApiId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenAppApi::getId);
        List<OpenAppApi> list = openAppApiService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openAppApi::getInfo", name = "获取  详细信息")
    public R<OpenAppApi> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppApiService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openAppApi::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenAppApi openAppApi) {
        return R.success(openAppApiService.save(openAppApi));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openAppApi::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenAppApi openAppApi) {
        return R.success(openAppApiService.updateById(openAppApi));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openAppApi::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppApiService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenAppApi.class)
    @Node(value = "openAppApi::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenAppApi openAppApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenAppApi> lqw = new LambdaQueryWrapper<OpenAppApi>();
        lqw.eq(openAppApi.getId() != null, OpenAppApi::getId ,openAppApi.getId());
        lqw.eq(openAppApi.getAppId() != null, OpenAppApi::getAppId ,openAppApi.getAppId());
        lqw.eq(openAppApi.getApiId() != null, OpenAppApi::getApiId ,openAppApi.getApiId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenAppApi::getId);
        return openAppApiService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openAppApi::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenAppApi.class, new ReadListener<OpenAppApi>() {
                @Override
                public void invoke(OpenAppApi openAppApi, AnalysisContext analysisContext) {
                    openAppApiService.save(openAppApi);
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