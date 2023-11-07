package work.soho.api.admin.service;

import java.util.Map;

public interface EmailApiService {
    /**
     * 发送指定邮件
     *
     * @param name
     * @param model
     */
    void sendEmail(String to, String name, Map<String,Object> model);

    /**
     * 发送邮件
     *
     * @param to
     * @param name
     * @param model
     * @param files
     */
    void sendEmail(String to, String name, Map<String, Object> model, Map<String, String> files);
}
