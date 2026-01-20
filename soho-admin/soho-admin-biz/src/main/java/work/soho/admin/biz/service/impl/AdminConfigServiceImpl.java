package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.biz.domain.AdminConfig;
import work.soho.admin.biz.domain.AdminConfigGroup;
import work.soho.admin.biz.mapper.AdminConfigGroupMapper;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.admin.biz.mapper.AdminConfigMapper;
import org.springframework.stereotype.Service;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.JacksonUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
* @author i
* @description 针对表【admin_config】的数据库操作Service实现
* @createDate 2022-04-05 23:01:25
*/
@Service("sohoConfig")
@RequiredArgsConstructor
public class AdminConfigServiceImpl extends ServiceImpl<AdminConfigMapper, AdminConfig>
    implements AdminConfigService, AdminConfigApiService {

    private final AdminConfigGroupMapper adminConfigGroupMapper;

    /**
     * 根据类型进行数据反序列化
     *
     * @param key
     * @return
     */
    public String getByKey(String key) {
        LambdaQueryWrapper<AdminConfig> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AdminConfig::getKey, key);
        AdminConfig adminConfig = getOne(lqw);
        if(adminConfig == null) {
            return null;
        }
        return adminConfig.getValue();
    }

    /**
     * 获取配置对象信息
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getByKey(String key, Class<T> clazz) {
        return getByKey(key, clazz, null);
    }

    /**
     * 获取值，支持默认值
     *
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T getByKey(String key, Class<T> clazz, T defaultValue) {
        String value = getByKey(key);
        try {
            if(value == null && defaultValue != null) {
                return defaultValue;
            }
            return JacksonUtils.toBean(value, clazz);
        } catch (Exception e) {
            //ignore
        }
        return defaultValue;
    }

    /**
     * 初始化配置信息
     *
     * @param adminConfigInitRequest
     * @return
     */
    public Boolean initItems(AdminConfigInitRequest adminConfigInitRequest) {
        if (adminConfigInitRequest == null) {
            return Boolean.FALSE;
        }
        if(adminConfigInitRequest.getGroupList() != null) {
            adminConfigInitRequest.getGroupList().stream().forEach(group -> {
                LambdaQueryWrapper<AdminConfigGroup> adminConfigGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
                adminConfigGroupLambdaQueryWrapper.eq(AdminConfigGroup::getKey, group.getKey());
                AdminConfigGroup dbGroup = adminConfigGroupMapper.selectOne(adminConfigGroupLambdaQueryWrapper);
                if(dbGroup == null) {
                    AdminConfigGroup configGroup = BeanUtils.copy(group, AdminConfigGroup.class);
                    configGroup.setCreatedTime(LocalDateTime.now());
                    adminConfigGroupMapper.insert(configGroup);
                }
            });
        }

        ArrayList<AdminConfigInitRequest.Item> items = adminConfigInitRequest.getItems();
        if (items == null || items.isEmpty()) {
            return Boolean.TRUE;
        }
        ArrayList<AdminConfig> saveDataList = (ArrayList<AdminConfig>) items.stream().filter(item -> {
            LambdaQueryWrapper<AdminConfig> adminConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
            adminConfigLambdaQueryWrapper.eq(AdminConfig::getKey, item.getKey());
            AdminConfig adminConfig = getOne(adminConfigLambdaQueryWrapper);
            return adminConfig == null;
        }).map(item -> {
            AdminConfig adminConfig = BeanUtils.copy(item, AdminConfig.class);
            adminConfig.setCreatedTime(LocalDateTime.now());
            adminConfig.setUpdatedTime(LocalDateTime.now());
            return adminConfig;
        }).collect(Collectors.toList());

        return saveBatch(saveDataList);
    }
}



