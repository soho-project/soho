package work.soho.api.admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Builder
public class AdminConfigInitRequest {
    private ArrayList<Group> groupList;
    private ArrayList<Item> items;


    /**
     * 配置分组
     */
    @Data
    @Builder
    public static class Group {
        /**
         * group key
         */
        @ApiModelProperty("组识别键")
        private String key;

        /**
         * 组名
         */
        @ApiModelProperty("组名")
        private String name;
    }

    /**
     * 配置单条数据
     */
    @Data
    @Builder
    public static class Item {
        /**
         * 配置文件分组名
         */
        @ApiModelProperty("配置文件分组名")
        private String groupKey;

        /**
         * 配置信息唯一识别key
         */
        @ApiModelProperty("配置信息唯一识别key")
        private String key;

        /**
         * 配置信息值
         */
        @ApiModelProperty("配置信息值")
        private String value;

        /**
         * 说明
         */
        @ApiModelProperty("说明")
        private String explain;

        /**
         * 配置信息类型
         */
        @ApiModelProperty("配置信息类型")
        private Integer type;
    }

    @Getter
    public static enum ItemType {
        TEXT(1, "文本"),
        JSON(2, "JSON"),
        BOOL(3, "布尔值"),
        UPLOAD(4, "上传");


        private int type;
        private String name;

        ItemType(int i, String name) {
            this.type = i;
            this.name = name;
        }
    }
}
