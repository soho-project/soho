package work.soho.user.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserSysConfig implements InitializingBean {
    private final String SYS_USER_CONFIG_GROUP = "user";

    // 默认头像
    private final String SYS_USER_DEFAULT_AVATAR_KEY = "user_default_avatar";
    private final String SYS_USER_DEFAULT_AVATAR = "https://soho-oss.oss-cn-shenzhen.aliyuncs.com/avatar/default.png";

    // 是否开启自动实名认证
    private final String SYS_USER_AUTO_REALNAME_KEY = "user_auto_realname";
    private final String SYS_USER_AUTO_REALNAME = "false";

    // 登录开发模式
    private final String SYS_USER_LOGIN_DEV = "user_login_dev";

    /**
     * 层级计算根用户
     */
    private final String ROOT_USER_ID = "performance_root_user_id";

    private final AdminConfigApiService adminConfigApiService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化组名
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(SYS_USER_CONFIG_GROUP).name("用户配置").build());

        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(AdminConfigInitRequest.Item.builder().groupKey(SYS_USER_CONFIG_GROUP).key(SYS_USER_DEFAULT_AVATAR_KEY).value(SYS_USER_DEFAULT_AVATAR).type(AdminConfigInitRequest.ItemType.UPLOAD.getType()).explain("用户默认头像地址").build());

        items.add(AdminConfigInitRequest.Item.builder().groupKey(SYS_USER_CONFIG_GROUP).key(SYS_USER_AUTO_REALNAME_KEY).value(SYS_USER_AUTO_REALNAME).type(AdminConfigInitRequest.ItemType.BOOL.getType()).explain("是否开启自动实名认证").build());

        items.add(AdminConfigInitRequest.Item.builder().groupKey(SYS_USER_CONFIG_GROUP).key(ROOT_USER_ID).value("1").type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("推荐层级用户根用户ID").build());

        items.add(AdminConfigInitRequest.Item.builder().groupKey(SYS_USER_CONFIG_GROUP).key(SYS_USER_LOGIN_DEV).value("false").type(AdminConfigInitRequest.ItemType.BOOL.getType()).explain("是否开启登录开发模式").build());

        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }

    /**
     * 获取用户默认头像地址
     *
     * @return
     */
    public String getDefaultAvatar() {
        return adminConfigApiService.getByKey(SYS_USER_DEFAULT_AVATAR_KEY, String.class, SYS_USER_DEFAULT_AVATAR);
    }

    /**
     * 是否开启自动实名认证
     *
     * @return
     */
    public Boolean getAutoRealname() {
        return adminConfigApiService.getByKey(SYS_USER_AUTO_REALNAME_KEY, Boolean.class, false);
    }

    /**
     * 获取推荐层级用户根用户ID
     *
     * @return
     */
    public Long getRootUserId() {
        return adminConfigApiService.getByKey(ROOT_USER_ID, Long.class, 1L);
    }

    /**
     * 获取登录开发模式
     *
     * @return
     */
    public Boolean getLoginDev() {
        return adminConfigApiService.getByKey(SYS_USER_LOGIN_DEV, Boolean.class, false);
    }
}
