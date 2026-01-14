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
import work.soho.open.biz.domain.OpenTicket;
import work.soho.open.biz.service.OpenTicketService;
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
@RequestMapping("/open/admin/openTicket" )
public class OpenTicketController {

    private final OpenTicketService openTicketService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "openTicket::list", name = "获取  列表")
    public R<PageSerializable<OpenTicket>> list(OpenTicket openTicket, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenTicket> lqw = new LambdaQueryWrapper<>();
        lqw.eq(openTicket.getId() != null, OpenTicket::getId ,openTicket.getId());
        lqw.like(StringUtils.isNotBlank(openTicket.getNo()),OpenTicket::getNo ,openTicket.getNo());
        lqw.eq(openTicket.getCategoryId() != null, OpenTicket::getCategoryId ,openTicket.getCategoryId());
        lqw.like(StringUtils.isNotBlank(openTicket.getTitle()),OpenTicket::getTitle ,openTicket.getTitle());
        lqw.like(StringUtils.isNotBlank(openTicket.getContent()),OpenTicket::getContent ,openTicket.getContent());
        lqw.eq(openTicket.getStatus() != null, OpenTicket::getStatus ,openTicket.getStatus());
        lqw.eq(openTicket.getAppId() != null, OpenTicket::getAppId ,openTicket.getAppId());
        lqw.eq(openTicket.getLastReadTime() != null, OpenTicket::getLastReadTime ,openTicket.getLastReadTime());
        lqw.eq(openTicket.getLastAnswerTime() != null, OpenTicket::getLastAnswerTime ,openTicket.getLastAnswerTime());
        lqw.eq(openTicket.getUpdatedTime() != null, OpenTicket::getUpdatedTime ,openTicket.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicket::getId);
        List<OpenTicket> list = openTicketService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openTicket::getInfo", name = "获取  详细信息")
    public R<OpenTicket> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openTicketService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "openTicket::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenTicket openTicket) {
        return R.success(openTicketService.save(openTicket));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "openTicket::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenTicket openTicket) {
        return R.success(openTicketService.updateById(openTicket));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openTicket::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openTicketService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出  Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = OpenTicket.class)
    @Node(value = "openTicket::exportExcel", name = "导出  Excel")
    public Object exportExcel(OpenTicket openTicket, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<OpenTicket> lqw = new LambdaQueryWrapper<OpenTicket>();
        lqw.eq(openTicket.getId() != null, OpenTicket::getId ,openTicket.getId());
        lqw.like(StringUtils.isNotBlank(openTicket.getNo()),OpenTicket::getNo ,openTicket.getNo());
        lqw.eq(openTicket.getCategoryId() != null, OpenTicket::getCategoryId ,openTicket.getCategoryId());
        lqw.like(StringUtils.isNotBlank(openTicket.getTitle()),OpenTicket::getTitle ,openTicket.getTitle());
        lqw.like(StringUtils.isNotBlank(openTicket.getContent()),OpenTicket::getContent ,openTicket.getContent());
        lqw.eq(openTicket.getStatus() != null, OpenTicket::getStatus ,openTicket.getStatus());
        lqw.eq(openTicket.getAppId() != null, OpenTicket::getAppId ,openTicket.getAppId());
        lqw.eq(openTicket.getLastReadTime() != null, OpenTicket::getLastReadTime ,openTicket.getLastReadTime());
        lqw.eq(openTicket.getLastAnswerTime() != null, OpenTicket::getLastAnswerTime ,openTicket.getLastAnswerTime());
        lqw.eq(openTicket.getUpdatedTime() != null, OpenTicket::getUpdatedTime ,openTicket.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenTicket::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(OpenTicket::getId);
        return openTicketService.list(lqw);
    }

    /**
     * 导入  Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "openTicket::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), OpenTicket.class, new ReadListener<OpenTicket>() {
                @Override
                public void invoke(OpenTicket openTicket, AnalysisContext analysisContext) {
                    openTicketService.save(openTicket);
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