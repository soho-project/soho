package com.yundasys.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryTrackBillResultResponse {
    private Boolean result;
    private String time;
    @JsonProperty("mailno")
    private String mailNo;
    private String remark;
    private String status;
    private List<Step> steps;

    @Data
    public static class Step {
        private Date time;
        private String status;
        private Action action;
        private String city;
        @JsonProperty("next_city")
        private String nextCity;
        private Integer station;
        @JsonProperty("station_name")
        private String stationName;
        @JsonProperty("station_type")
        private Integer stationType;
        private String description;
        private String phone;
        private String signer;
    }

    @Getter
    public static enum Action {
        ACCEPT("ACCEPT", "收件扫描"),
        GOT("GOT", "揽件扫描"),
        ARRIVAL("ARRIVAL", "入中转"),
        DEPARTURE("DEPARTURE", "出中转"),
        SENT("SENT", "派件中"),
        INBOUND("INBOUND", "第三方代收入库"),
        SIGNED("SIGNED", "已签收"),
        OUTBOUND("OUTBOUND", "第三方代收快递员取出"),
        SIGNFAIL("SIGNFAIL", "签收失败"),
        RETURN("RETURN", "退回件"),
        ISSUE("ISSUE", "问题件"),
        REJECTION("REJECTION", "拒收"),
        OTHER("OTHER", "其他"),
        OVERSEA_IN("OVERSEA_IN", "入库扫描"),
        OVERSEA_OUT("OVERSEA_OUT", "出库扫描"),
        CLEARANCE_START("CLEARANCE_START", "清关开始"),
        CLEARANCE_FINISH("CLEARANCE_FINISH", "清关结束"),
        CLEARANCE_FAIL("CLEARANCE_FAIL", "清关失败"),
        OVERSEA_ARRIVAL("OVERSEA_ARRIVAL", "干线到达"),
        OVERSEA_DEPARTURE("OVERSEA_DEPARTURE", "干线离开"),
        TRANSFER("TRANSFER", "转单");

        private String value;
        private String name;

        Action(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Action fromValue(String value) {
            for (Action action : values()) {
                if (action.value.equals(value)) {
                    return action;
                }
            }
            return null;
        }
    }
}
