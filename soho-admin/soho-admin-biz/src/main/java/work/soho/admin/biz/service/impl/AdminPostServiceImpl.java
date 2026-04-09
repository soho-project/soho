package work.soho.admin.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.admin.biz.domain.AdminPost;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.mapper.AdminPostMapper;
import work.soho.admin.biz.mapper.AdminUserMapper;
import work.soho.admin.biz.service.AdminPostService;

/**
 * 岗位服务实现。
 */
@Service
public class AdminPostServiceImpl extends ServiceImpl<AdminPostMapper, AdminPost> implements AdminPostService {
    private final AdminUserMapper adminUserMapper;

    public AdminPostServiceImpl(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    /**
     * 保存岗位。
     *
     * @param adminPost 岗位信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(AdminPost adminPost) {
        Assert.notBlank(adminPost.getName(), "岗位名称不能为空");
        if (adminPost.getSort() == null) {
            adminPost.setSort(0);
        }
        if (adminPost.getStatus() == null) {
            adminPost.setStatus(1);
        }
        saveOrUpdate(adminPost);
    }

    /**
     * 删除岗位。
     *
     * @param id 岗位ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePost(Long id) {
        AdminPost adminPost = getById(id);
        Assert.notNull(adminPost, "岗位不存在");

        Long userCount = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getPostId, id)
                .eq(AdminUser::getIsDeleted, 0));
        Assert.isFalse(userCount != null && userCount > 0, "岗位下存在用户，无法删除");

        removeById(id);
    }
}
