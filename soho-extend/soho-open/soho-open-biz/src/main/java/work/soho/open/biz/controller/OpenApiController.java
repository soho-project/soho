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
import work.soho.open.biz.domain.OpenApi;
import work.soho.open.biz.service.OpenApiService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 开放平台apiController
 *
 * @author fang
 */
@Api(tags = "开放平台api")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/admin/openApi" )
public class OpenApiController {

    private final OpenApiService openApiService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询开放平台api列表
     */
    @GetMapping("/list")
    @Node(value = "openApi::list", name = "获取 开放平台api 列表")
    public R<PageSerializable<OpenApi>> list(OpenApi openApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApi> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openApi.getId() != null, OpenApi::getId ,openApi.getId());
        lqw.like(StringUtils.isNotBlank(openApi.getApiCode()),OpenApi::getApiCode ,openApi.getApiCode());
        lqw.like(StringUtils.isNotBlank(openApi.getApiName()),OpenApi::getApiName ,openApi.getApiName());
        lqw.like(StringUtils.isNotBlank(openApi.getMethod()),OpenApi::getMethod ,openApi.getMethod());
        lqw.like(StringUtils.isNotBlank(openApi.getPath()),OpenApi::getPath ,openApi.getPath());
        lqw.like(StringUtils.isNotBlank(openApi.getVersion()),OpenApi::getVersion ,openApi.getVersion());
        lqw.eq(openApi.getNeedAuth() != null, OpenApi::getNeedAuth ,openApi.getNeedAuth());
        lqw.eq(openApi.getStatus() != null, OpenApi::getStatus ,openApi.getStatus());
        lqw.like(StringUtils.isNotBlank(openApi.getDescription()),OpenApi::getDescription ,openApi.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApi.getUpdatedTime() != null, OpenApi::getUpdatedTime ,openApi.getUpdatedTime());
        lqw.orderByDesc(OpenApi::getId);
        List<OpenApi> list = openApiService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台api详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openApi::getInfo", name = "获取 开放平台api 详细信息")
    public R<OpenApi> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openApiService.getById(id));
    }

    /**
     * 新增开放平台api
     */
    @PostMapping
    @Node(value = "openApi::add", name = "新增 开放平台api")
    public R<Boolean> add(@RequestBody OpenApi openApi) {
        return R.success(openApiService.save(openApi));
    }

    /**
     * 修改开放平台api
     */
    @PutMapping
    @Node(value = "openApi::edit", name = "修改 开放平台api")
    public R<Boolean> edit(@RequestBody OpenApi openApi) {
        return R.success(openApiService.updateById(openApi));
    }

    /**
     * 删除开放平台api
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openApi::remove", name = "删除 开放平台api")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openApiService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该开放平台api 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "openApi::options", name = "获取 开放平台api 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OpenApi> list = openApiService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(OpenApi item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getApiName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 开放平台api Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenApi.class)
    @Node(value = "openApi::exportExcel", name = "导出 开放平台api Excel")
    public Object exportExcel(OpenApi openApi, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenApi> lqw = new LambdaQueryWrapper<OpenApi>();
        lqw.eq(openApi.getId() != null, OpenApi::getId ,openApi.getId());
        lqw.like(StringUtils.isNotBlank(openApi.getApiCode()),OpenApi::getApiCode ,openApi.getApiCode());
        lqw.like(StringUtils.isNotBlank(openApi.getApiName()),OpenApi::getApiName ,openApi.getApiName());
        lqw.like(StringUtils.isNotBlank(openApi.getMethod()),OpenApi::getMethod ,openApi.getMethod());
        lqw.like(StringUtils.isNotBlank(openApi.getPath()),OpenApi::getPath ,openApi.getPath());
        lqw.like(StringUtils.isNotBlank(openApi.getVersion()),OpenApi::getVersion ,openApi.getVersion());
        lqw.eq(openApi.getNeedAuth() != null, OpenApi::getNeedAuth ,openApi.getNeedAuth());
        lqw.eq(openApi.getStatus() != null, OpenApi::getStatus ,openApi.getStatus());
        lqw.like(StringUtils.isNotBlank(openApi.getDescription()),OpenApi::getDescription ,openApi.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApi::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApi.getUpdatedTime() != null, OpenApi::getUpdatedTime ,openApi.getUpdatedTime());
        lqw.orderByDesc(OpenApi::getId);
        return openApiService.list(lqw);
    }

    /**
     * 导入 开放平台api Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openApi::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenApi.class, new ReadListener<OpenApi>() {
                @Override
                public void invoke(OpenApi openApi, AnalysisContext analysisContext) {
                    openApiService.save(openApi);
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