package work.soho.api.admin.vo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class OptionVo <T, X>{
    private T key;
    private X value;

    public void setKey(T key) {
        this.key = key;
    }

    public void setValue(X value) {
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public X getValue() {
        return value;
    }
}
