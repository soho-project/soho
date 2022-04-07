package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageInfo;
import work.soho.common.core.result.R;
import work.soho.admin.domain.Hello;
import work.soho.admin.service.HelloService;

/**
 * helloController
 *
 * @author i
 * @date 2022-04-05 21:14:29
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/hello" )
public class HelloController extends BaseController {

    private final HelloService helloService;

    /**
     * 查询hello列表
     */
    @GetMapping("/list")
    public R<PageInfo> list(Hello hello)
    {
        startPage();
        LambdaQueryWrapper<Hello> lqw = new LambdaQueryWrapper<Hello>();

        if (hello.getId() != null){
            lqw.eq(Hello::getId ,hello.getId());
        }
        if (StringUtils.isNotBlank(hello.getName())){
            lqw.like(Hello::getName ,hello.getName());
        }
        if (StringUtils.isNotBlank(hello.getValue())){
            lqw.like(Hello::getValue ,hello.getValue());
        }
        List<Hello> list = helloService.list(lqw);
        return R.success((PageInfo)list);
    }

    /**
     * 获取hello详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<Hello> getInfo(@PathVariable("id" ) Long id) {
        return R.success(helloService.getById(id));
    }

    /**
     * 新增hello
     */
    @PostMapping
    public R<Boolean> add(@RequestBody Hello hello) {
        return R.success(helloService.save(hello));
    }

    /**
     * 修改hello
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody Hello hello) {
        return R.success(helloService.updateById(hello));
    }

    /**
     * 删除hello
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(helloService.removeByIds(Arrays.asList(ids)));
    }
}
