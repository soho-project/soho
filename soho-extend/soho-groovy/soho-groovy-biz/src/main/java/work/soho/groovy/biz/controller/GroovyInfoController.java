package work.soho.groovy.biz.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

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
import work.soho.api.admin.annotation.Node;
import work.soho.groovy.biz.domain.GroovyInfo;
import work.soho.groovy.biz.service.GroovyExecutorService;
import work.soho.groovy.biz.service.GroovyInfoService;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.groovy.biz.vo.TestCodeVO;

/**
 * groovy代码Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/groovyInfo" )
public class GroovyInfoController {

    private final GroovyInfoService groovyInfoService;

    private final GroovyExecutorService groovyExecutorService;

    /**
     * 查询groovy代码列表
     */
    @GetMapping("/list")
    @Node(value = "groovyInfo::list", name = "groovy代码;;列表")
    public R<PageSerializable<GroovyInfo>> list(GroovyInfo groovyInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<GroovyInfo> lqw = new LambdaQueryWrapper<GroovyInfo>();
        lqw.eq(groovyInfo.getId() != null, GroovyInfo::getId ,groovyInfo.getId());
        lqw.eq(groovyInfo.getGroupId() != null, GroovyInfo::getGroupId ,groovyInfo.getGroupId());
        lqw.like(StringUtils.isNotBlank(groovyInfo.getName()),GroovyInfo::getName ,groovyInfo.getName());
        lqw.like(StringUtils.isNotBlank(groovyInfo.getCode()),GroovyInfo::getCode ,groovyInfo.getCode());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, GroovyInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, GroovyInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(groovyInfo.getUpdatedTime() != null, GroovyInfo::getUpdatedTime ,groovyInfo.getUpdatedTime());
        List<GroovyInfo> list = groovyInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取groovy代码详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "groovyInfo::getInfo", name = "groovy代码;;详细信息")
    public R<GroovyInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(groovyInfoService.getById(id));
    }

    /**
     * 新增groovy代码
     */
    @PostMapping
    @Node(value = "groovyInfo::add", name = "groovy代码;;新增")
    public R<Boolean> add(@RequestBody GroovyInfo groovyInfo) {
        groovyInfo.setUpdatedTime(LocalDateTime.now());
        groovyInfo.setCreatedTime(LocalDateTime.now());
        return R.success(groovyInfoService.save(groovyInfo));
    }

    /**
     * 修改groovy代码
     */
    @PutMapping
    @Node(value = "groovyInfo::edit", name = "groovy代码;;修改")
    public R<Boolean> edit(@RequestBody GroovyInfo groovyInfo) {
        groovyInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(groovyInfoService.updateById(groovyInfo));
    }

    /**
     * 删除groovy代码
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "groovyInfo::remove", name = "groovy代码;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(groovyInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 对代码进行在线测试
     *
     * @param code
     * @return
     */
    @PostMapping("/test")
    public R<Object> testCode(@RequestBody TestCodeVO code) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", "success");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalStdout = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
        try {
            map.put("result", groovyExecutorService.execute(code.getCode() +"\r\n"+ code.getTestCode()));

            String capturedOutput = outputStream.toString();
            map.put("output",capturedOutput);
            return R.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", "exception");
            map.put("result", e.getStackTrace());
            String capturedOutput = outputStream.toString();
            map.put("output",capturedOutput);
            return R.success(map);
        } finally {
            System.setOut(originalStdout);
            System.setErr(originalErr);
        }

    }
}
