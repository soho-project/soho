package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenApiEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Method {
        DELETE("DELETE","DELETE"),
        POST("POST","POST"),
        GET("GET","GET"),
        PUT("PUT","PUT"),
        PATCH("PATCH","PATCH");
        private final String id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum NeedAuth {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }
}