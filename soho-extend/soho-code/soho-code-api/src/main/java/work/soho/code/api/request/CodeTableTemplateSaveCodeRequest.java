package work.soho.code.api.request;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CodeTableTemplateSaveCodeRequest {
    private Integer id;
    private ArrayList<Integer> templateId;
    private String path;
}
