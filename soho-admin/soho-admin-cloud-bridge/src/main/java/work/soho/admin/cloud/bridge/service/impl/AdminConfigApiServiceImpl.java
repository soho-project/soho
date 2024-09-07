package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.bridge.feign.AdminConfigApiServiceFeign;
import work.soho.api.admin.request.AdminConfigInitRequest;
import work.soho.api.admin.service.AdminConfigApiService;

@Service
@RequiredArgsConstructor
public class AdminConfigApiServiceImpl implements AdminConfigApiService {
    private final AdminConfigApiServiceFeign adminConfigApiServiceFeign;

    @Override
    public <T> T getByKey(String key, Class<T> clazz) {
        String val = adminConfigApiServiceFeign.getByKey(key);
        return val == null ? null : clazz.cast(val);
    }

    @Override
    public <T> T getByKey(String key, Class<T> clazz, T defaultValue) {
        String value = adminConfigApiServiceFeign.getByKey(key);
        return value == null ? defaultValue : clazz.cast(value);
    }

    @Override
    public Boolean initItems(AdminConfigInitRequest adminConfigInitRequest) {
        return adminConfigApiServiceFeign.initItems(adminConfigInitRequest);
    }
}
