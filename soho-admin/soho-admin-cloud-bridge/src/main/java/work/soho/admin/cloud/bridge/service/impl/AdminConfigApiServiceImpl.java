package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.bridge.feign.AdminConfigApiServiceFeign;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;
import work.soho.common.core.util.JacksonUtils;

@Service
@RequiredArgsConstructor
public class AdminConfigApiServiceImpl implements AdminConfigApiService {
    private final AdminConfigApiServiceFeign adminConfigApiServiceFeign;

    @Override
    public <T> T getByKey(String key, Class<T> clazz) {
        return getByKey(key, clazz, null);
    }

    @Override
    public <T> T getByKey(String key, Class<T> clazz, T defaultValue) {
        String value = adminConfigApiServiceFeign.getByKey(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return JacksonUtils.toBean(value, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean initItems(AdminConfigInitRequest adminConfigInitRequest) {
        return adminConfigApiServiceFeign.initItems(adminConfigInitRequest);
    }
}
