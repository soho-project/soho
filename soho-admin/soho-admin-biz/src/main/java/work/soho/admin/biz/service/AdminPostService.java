package work.soho.admin.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.biz.domain.AdminPost;

/**
 * 岗位服务。
 */
public interface AdminPostService extends IService<AdminPost> {

    /**
     * 保存岗位。
     *
     * @param adminPost 岗位信息
     */
    void savePost(AdminPost adminPost);

    /**
     * 删除岗位。
     *
     * @param id 岗位ID
     */
    void removePost(Long id);
}
