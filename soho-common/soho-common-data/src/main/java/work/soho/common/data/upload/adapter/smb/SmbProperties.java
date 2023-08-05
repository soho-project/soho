package work.soho.common.data.upload.adapter.smb;

import lombok.Data;

@Data
public class SmbProperties {
    private String hostName;
    private String shareName;
    private String username;
    private String password;
    private String domain = "DOMAIN";
    private String urlPrefix;
}
