package work.soho.api.admin.request;

import lombok.Data;

@Data
public class AdminContentRequest extends BetweenDateRequest {
    private String title;
    private Long categoryId;
}
