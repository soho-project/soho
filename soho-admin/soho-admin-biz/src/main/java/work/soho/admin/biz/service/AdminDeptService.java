package work.soho.admin.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.biz.domain.AdminDept;

import java.util.List;

/**
 * 部门服务。
 */
public interface AdminDeptService extends IService<AdminDept> {

    /**
     * 获取部门树。
     *
     * @return 部门树列表
     */
    List<AdminDept> tree();

    /**
     * 保存部门。
     *
     * @param adminDept 部门信息
     */
    void saveDept(AdminDept adminDept);

    /**
     * 删除部门。
     *
     * @param id 部门ID
     */
    void removeDept(Long id);
}
