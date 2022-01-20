package work.soho.api.admin.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Hello implements Serializable {
    private Integer id;
    private String name;
    private String value;
}
