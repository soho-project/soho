package work.soho.pay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletDto {
    private Integer payInfoId;
    private Long userId;
    private String userName;
    private String userMobile;
    private String idCard;
    private String idCardType = "10";
}
