package work.soho.longlink.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 在线状态枚举
 */
@Getter
@RequiredArgsConstructor
public enum OnlineEnum {
    NOT_ONLINE(0, "下线"),
    ONLINE(1, "在线")
    ;

    private final int id;
    private final String name;
}
