package work.soho.admin.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminSysConfig implements InitializingBean {
    private final AdminConfigApiService adminConfigApiService;
    private final String DEFAULT_GROUP_NAME = "管理后台";
    private final String DEFAULT_GROUP_KEY = "admin";

    private final String ADMIN_OPERATION_LOG_ENABLE_KEY = "admin_operation_log_enable";
    private final String ADMIN_OPERATION_LOG_ENABLE_VALUE = "true";

    private final String ADMIN_OPERATION_LOG_METHODS_KEY = "admin_operation_log_methods";
    private final String ADMIN_OPERATION_LOG_METHODS = "POST,PUT,DELETE";

    //登录验证码开关
    private final String ADMIN_LOGIN_CAPTCHA_ENABLE_KEY = "login_use_captcha";
    //后台通知方式
    private final String ADMIN_NOTICE_ADAPTER_KEY = "admin-notice-adapter";
    // 后台用户是否关联普通用户
    private final String ADMIN_USER_RELATION_ENABLE_KEY = "admin_user_relation_enable";

    /**
     * 获取是否开启操作的日志
     *
     * @return
     */
    public Boolean getAdminOperationLogEnable() {
        return adminConfigApiService.getByKey(ADMIN_OPERATION_LOG_ENABLE_KEY, Boolean.class, Boolean.TRUE);
    }

    /**
     * 获取需要进行日志记录的请求方法
     *
     * @return
     */
    public Set<String> getAdminOperationLogMethods() {
        String methods = adminConfigApiService.getByKey(ADMIN_OPERATION_LOG_METHODS_KEY, String.class, ADMIN_OPERATION_LOG_METHODS);
        String[] strSets = methods.split(",");
        return new HashSet<>(Arrays.asList(strSets));
    }

    /**
     * 获取后台用户登录是否开启验证码
     *
     * @return
     */
    public Boolean getAdminLoginCaptchaEnable() {
        return adminConfigApiService.getByKey(ADMIN_LOGIN_CAPTCHA_ENABLE_KEY, Boolean.class, false);
    }

    /**
     * 获取后台通知方式
     *
     * @return
     */
    public String getAdminNoticeAdapter() {
        return adminConfigApiService.getByKey(ADMIN_NOTICE_ADAPTER_KEY, String.class, "none");
    }

    // 获取管理员是否关联普通用户
    public Boolean getAdminUserRelationEnable() {
        return adminConfigApiService.getByKey(ADMIN_USER_RELATION_ENABLE_KEY, Boolean.class, false);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //构建组
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(DEFAULT_GROUP_KEY).name(DEFAULT_GROUP_NAME).build());

        //构建单元配置
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        //是否开启登录验证码
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_KEY)
                        .explain("\t后台登录是否开启验证码")
                .type(AdminConfigInitRequest.ItemType.BOOL.getType()).key(ADMIN_LOGIN_CAPTCHA_ENABLE_KEY).value("false").build());
        //后台通知方式
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_KEY)
                        .explain("通知驱动方式；none:不通知,polling:轮训,lon")
                .type(AdminConfigInitRequest.ItemType.BOOL.getType()).key(ADMIN_NOTICE_ADAPTER_KEY).value("none").build());
        //是否开启操作日志
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_KEY)
                        .explain("是否开启后台操作日志")
                .type(AdminConfigInitRequest.ItemType.BOOL.getType()).key(ADMIN_OPERATION_LOG_ENABLE_KEY).value(ADMIN_OPERATION_LOG_ENABLE_VALUE).build());
        //操作日志支持方法
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_KEY)
                        .explain("操作日志记录的请求方法；例如: POST(创建);PUT(更新);DELETE(删除)")
                .type(AdminConfigInitRequest.ItemType.TEXT.getType()).key(ADMIN_OPERATION_LOG_METHODS_KEY).value(ADMIN_OPERATION_LOG_METHODS).build());

        // 后台用户是否关联普通用户
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_KEY)
                .explain("后台用户是否关联普通用户")
                .type(AdminConfigInitRequest.ItemType.BOOL.getType()).key(ADMIN_USER_RELATION_ENABLE_KEY).value("false").build());

        //注册配置信息
        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }
}
