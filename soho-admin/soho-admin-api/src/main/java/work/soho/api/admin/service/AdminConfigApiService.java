package work.soho.api.admin.service;

import work.soho.api.admin.request.AdminConfigInitRequest;

public interface AdminConfigApiService {
    <T> T getByKey(String key, Class<T> clazz);

    <T> T getByKey(String key, Class<T> clazz, T defaultValue);

    Boolean initItems(AdminConfigInitRequest adminConfigInitRequest);
}
