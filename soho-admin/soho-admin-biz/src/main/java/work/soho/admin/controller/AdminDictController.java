package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminDict;
import work.soho.admin.service.AdminDictService;
import work.soho.api.admin.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典
 */
@Api(tags = "系统字典")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminDict")
public class AdminDictController {

    private final AdminDictService adminDictService;

    /**
     * 查询字典表列表
     */
    @GetMapping("/list")
    @Node(value = "adminDict::list", name = "字典表;;option:dict_key~dict_value列表")
    public R<PageSerializable<AdminDict>> list(AdminDict adminDict) {
        LambdaQueryWrapper<AdminDict> lqw = new LambdaQueryWrapper<>();
        if (adminDict.getCode() != null) {
            lqw.eq(AdminDict::getCode, adminDict.getCode());
        }
        if (adminDict.getDictValue() != null) {
            lqw.eq(AdminDict::getDictValue, adminDict.getDictValue());
        }
        if (adminDict.getParentId() != null) {
            lqw.eq(AdminDict::getParentId, adminDict.getParentId());
        } else {
            lqw.eq(AdminDict::getParentId, 0);
        }

        PageUtils.startPage();
        List<AdminDict> list = adminDictService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取字典表详细信息
     */
    @GetMapping(value = "/{id}")
    @Node(value = "adminDict::getInfo", name = "字典表;;option:dict_key~dict_value详细信息")
    public R<AdminDict> getInfo(@PathVariable("id") Long id) {
        return R.success(adminDictService.getById(id));
    }

    /**
     * 新增字典表
     */
    @PostMapping
    @Node(value = "adminDict::add", name = "字典表;;option:dict_key~dict_value新增")
    public R<Boolean> add(@RequestBody AdminDict adminDict) {
        long count = adminDictService.count(
                Wrappers.<AdminDict>lambdaQuery()
                        .eq(AdminDict::getCode, adminDict.getCode())
                        .eq(AdminDict::getDictKey, adminDict.getDictKey())
        );
        if (count > 0) {
            return R.error("当前字典键值已存在!");
        }

        return R.success(adminDictService.save(adminDict));
    }

    /**
     * 修改字典表
     */
    @PutMapping
    @Node(value = "adminDict::edit", name = "字典表;;option:dict_key~dict_value修改")
    public R<Boolean> edit(@RequestBody AdminDict adminDict) {
        return R.success(adminDictService.updateById(adminDict));
    }

    /**
     * 删除字典表
     */
    @DeleteMapping("/{ids}")
    @Node(value = "adminDict::remove", name = "字典表;;option:dict_key~dict_value删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminDictService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该字典表 options:dict_key-dict_value
     *
     * @return
     */
    @GetMapping("/options")
    public R<Map<Integer, String>> options(@RequestParam("code") String code) {
        List<AdminDict> list = adminDictService.list(
                Wrappers.<AdminDict>lambdaQuery()
                        .eq(AdminDict::getCode, code)
                        .gt(AdminDict::getParentId, 0)
                        .orderByAsc(AdminDict::getSort)
        );

        Map<Integer, String> options = list.stream().collect(Collectors.toMap(AdminDict::getDictKey, AdminDict::getDictValue));
        return R.success(options);
    }
}