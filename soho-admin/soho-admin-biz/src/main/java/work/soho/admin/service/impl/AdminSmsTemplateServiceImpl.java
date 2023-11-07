package work.soho.admin.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.domain.AdminSmsTemplate;
import work.soho.admin.mapper.AdminSmsTemplateMapper;
import work.soho.admin.service.AdminSmsTemplateService;
import work.soho.api.admin.service.SmsApiService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.data.sms.Message;
import work.soho.common.data.sms.utils.SmsUtils;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminSmsTemplateServiceImpl extends ServiceImpl<AdminSmsTemplateMapper, AdminSmsTemplate>
    implements AdminSmsTemplateService, SmsApiService {

    @Value("${sms.defaultChannel}")
    private String defaultAdapterName;

    @Override
    public void sendSms(String phone, String name, Map<String, String> model) {
        try {
            LambdaQueryWrapper<AdminSmsTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AdminSmsTemplate::getName, name);
            lambdaQueryWrapper.eq(AdminSmsTemplate::getAdapterName, defaultAdapterName);
            AdminSmsTemplate adminSmsTemplate = getOne(lambdaQueryWrapper);
            Assert.notNull(adminSmsTemplate, "没有找到短信模板");

            Message message = new Message();
            message.setSignName(adminSmsTemplate.getSignName())
                    .setPhoneNumbers(phone)
                    .setTemplateCode(adminSmsTemplate.getTemplateCode())
                    .setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()))
                    .setParams((HashMap<String, String>) model);
            //默认通道发送短信
            SmsUtils.sendSms(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
