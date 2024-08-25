package work.soho.common.data.ip2region;

import lombok.Data;

@Data
public class RegionInfo {
    private String country;
    private String province;
    private String city;
    private String isp;
}
