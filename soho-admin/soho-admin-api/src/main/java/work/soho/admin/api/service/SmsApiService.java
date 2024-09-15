package work.soho.admin.api.service;

import java.util.Map;

public interface SmsApiService {
    /**
     * 发送指定短信
     *
     * @param name
     * @param model
     */
    void sendSms(String phone, String name, Map<String,String> model);
}
