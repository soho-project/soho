package work.soho.admin.api.request;

import lombok.Data;

@Data
public class AdminContentRequest extends BetweenDateRequest {
    private String title;
    private Long categoryId;
}
