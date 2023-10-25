package work.soho.api.admin.service;

public interface AdminConfigApiService {
    <T> T getByKey(String key, Class<T> clazz);

    <T> T getByKey(String key, Class<T> clazz, T defaultValue);
}
