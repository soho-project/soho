package work.soho.mobile.verification.config;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.api.admin.service.AdminConfigApiService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SohoMobileVerificationConfig {

    private final AdminConfigApiService adminConfigApiService;

    private final static String PHONE_SMS_NUMBERS = "receiving-verification-codes-numbers";

    /**
     * 获取接收短信的手机号码
     *
     * @return
     */
    public ArrayList<String> getPhoneNumbers() {
        String body = adminConfigApiService.getByKey(PHONE_SMS_NUMBERS, String.class);
        if(StringUtils.isEmpty(body)) {
            return new ArrayList<>();
        }
        return JacksonUtils.toBean(body, new TypeReference<ArrayList<String>>() {});
    }
}
