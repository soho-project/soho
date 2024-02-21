package work.soho.mobile.verification.config;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import work.soho.api.admin.request.AdminConfigInitRequest;
import work.soho.api.admin.service.AdminConfigApiService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SohoMobileVerificationConfig implements InitializingBean {

    private final AdminConfigApiService adminConfigApiService;

    private final static String DEFAULT_GROUP = "public";

    private final static String PHONE_SMS_NUMBERS = "receiving-verification-codes-numbers";

    private final static String ANDROID_ACCESS_KEY = "mobile-verification-android-access-token";

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

    @Override
    public void afterPropertiesSet() throws Exception {
        AdminConfigInitRequest.Item item = AdminConfigInitRequest.Item.builder()
                .key(PHONE_SMS_NUMBERS)
                .groupKey(DEFAULT_GROUP)
                .explain("接收手机短信的手机号码")
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .build();
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(item);
        items.add(AdminConfigInitRequest.Item.builder().key(ANDROID_ACCESS_KEY).groupKey(DEFAULT_GROUP).value("123456").type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("Android端访问密钥").build());
        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).build());
    }
}
