package work.soho.user.api.request;

import lombok.Data;

@Data
public class ChangePhoneRequest {
    private String newPhone;
    private String newPhoneCaptcha;
    private String captcha;
}
