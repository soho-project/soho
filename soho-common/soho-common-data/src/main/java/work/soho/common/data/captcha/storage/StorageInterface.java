package work.soho.common.data.captcha.storage;

public interface StorageInterface {
    void set(String key, Object value);
    Object get(String key);
}
