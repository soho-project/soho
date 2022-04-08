package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminConfig;
import work.soho.admin.service.AdminConfigService;
import work.soho.admin.mapper.AdminConfigMapper;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.JacksonUtils;

/**
* @author i
* @description 针对表【admin_config】的数据库操作Service实现
* @createDate 2022-04-05 23:01:25
*/
@Service
public class AdminConfigServiceImpl extends ServiceImpl<AdminConfigMapper, AdminConfig>
    implements AdminConfigService{
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
        String value = getByKey(key);
        return JacksonUtils.toBean(value, clazz);
    }
}




