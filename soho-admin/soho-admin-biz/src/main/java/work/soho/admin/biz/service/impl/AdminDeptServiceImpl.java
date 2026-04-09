package work.soho.admin.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.admin.biz.domain.AdminDept;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.mapper.AdminDeptMapper;
import work.soho.admin.biz.mapper.AdminUserMapper;
import work.soho.admin.biz.service.AdminDeptService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 部门服务实现。
 */
@Service
public class AdminDeptServiceImpl extends ServiceImpl<AdminDeptMapper, AdminDept> implements AdminDeptService {
    private final AdminUserMapper adminUserMapper;

    public AdminDeptServiceImpl(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    /**
     * 获取部门树。
     *
     * @return 部门树列表
     */
    @Override
    public List<AdminDept> tree() {
        List<AdminDept> list = list(new LambdaQueryWrapper<AdminDept>()
                .orderByAsc(AdminDept::getSort)
                .orderByAsc(AdminDept::getId));
        return buildTree(list);
    }

    /**
     * 保存部门。
     *
     * @param adminDept 部门信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDept(AdminDept adminDept) {
        Assert.notBlank(adminDept.getName(), "部门名称不能为空");
        normalizeDept(adminDept);
        validateParent(adminDept);
        saveOrUpdate(adminDept);
    }

    /**
     * 删除部门。
     *
     * @param id 部门ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDept(Long id) {
        AdminDept adminDept = getById(id);
        Assert.notNull(adminDept, "部门不存在");

        long childCount = count(new LambdaQueryWrapper<AdminDept>().eq(AdminDept::getParentId, id));
        Assert.isFalse(childCount > 0, "请先删除子部门");

        Long userCount = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getDeptId, id)
                .eq(AdminUser::getIsDeleted, 0));
        Assert.isFalse(userCount != null && userCount > 0, "部门下存在用户，无法删除");

        removeById(id);
    }

    /**
     * 规范化部门默认值。
     *
     * @param adminDept 部门信息
     */
    private void normalizeDept(AdminDept adminDept) {
        if (adminDept.getParentId() == null) {
            adminDept.setParentId(0L);
        }
        if (adminDept.getSort() == null) {
            adminDept.setSort(0);
        }
        if (adminDept.getStatus() == null) {
            adminDept.setStatus(1);
        }
    }

    /**
     * 校验部门父子关系。
     *
     * @param adminDept 部门信息
     */
    private void validateParent(AdminDept adminDept) {
        Long parentId = adminDept.getParentId();
        if (Objects.equals(parentId, adminDept.getId())) {
            throw new IllegalArgumentException("上级部门不能选择自己");
        }

        if (parentId != null && parentId > 0) {
            AdminDept parentDept = getById(parentId);
            Assert.notNull(parentDept, "上级部门不存在");
        }

        if (adminDept.getId() != null && parentId != null && parentId > 0) {
            List<AdminDept> allDeptList = list();
            Map<Long, AdminDept> deptMap = allDeptList.stream()
                    .collect(Collectors.toMap(AdminDept::getId, Function.identity()));
            Long currentParentId = parentId;
            while (currentParentId != null && currentParentId > 0) {
                if (Objects.equals(currentParentId, adminDept.getId())) {
                    throw new IllegalArgumentException("上级部门不能选择当前部门的子节点");
                }
                AdminDept currentParent = deptMap.get(currentParentId);
                currentParentId = currentParent == null ? null : currentParent.getParentId();
            }
        }
    }

    /**
     * 构建部门树。
     *
     * @param list 部门列表
     * @return 树结构结果
     */
    private List<AdminDept> buildTree(List<AdminDept> list) {
        Map<Long, List<AdminDept>> childrenMap = list.stream()
                .collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        list.forEach(item -> item.setChildren(sortChildren(childrenMap.getOrDefault(item.getId(), new ArrayList<>()))));
        return sortChildren(childrenMap.getOrDefault(0L, new ArrayList<>()));
    }

    /**
     * 对子节点进行稳定排序。
     *
     * @param list 子节点列表
     * @return 排序后的列表
     */
    private List<AdminDept> sortChildren(List<AdminDept> list) {
        return list.stream()
                .sorted(Comparator.comparing(AdminDept::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(AdminDept::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());
    }
}
