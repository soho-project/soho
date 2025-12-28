package com.zto.zop.api;

import lombok.Data;

@Data
public class AbstructApi<Q, R> {
    private String baseUrl;

    public R execute(Q query) {
        return null;
    }
}
