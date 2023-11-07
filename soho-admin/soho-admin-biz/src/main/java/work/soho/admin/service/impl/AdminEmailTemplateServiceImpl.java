package work.soho.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.domain.AdminEmailTemplate;
import work.soho.admin.mapper.AdminEmailTemplateMapper;
import work.soho.admin.service.AdminEmailTemplateService;
import work.soho.admin.utils.TemplateResolverUtils;
import work.soho.api.admin.service.EmailApiService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminEmailTemplateServiceImpl extends ServiceImpl<AdminEmailTemplateMapper, AdminEmailTemplate>
    implements AdminEmailTemplateService, EmailApiService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Override
    public void sendEmail(String to, String name, Map<String, Object> model) {
        sendEmail(to, name, model, null);
    }


    public void sendEmail(String to, String name, Map<String, Object> model, Map<String, String> files) {
        try {
            LambdaQueryWrapper<AdminEmailTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AdminEmailTemplate::getName, name);
            AdminEmailTemplate emailTemplate = getOne(lambdaQueryWrapper);
            String title = TemplateResolverUtils.resolverString(emailTemplate.getTitle(), model);
            String body = TemplateResolverUtils.resolverHtml(emailTemplate.getBody(), model);


            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(body, true);
            helper.setSentDate(new Date());
            //添加附件
            if(files !=null && files.size()>0) {
                files.entrySet().stream().forEach(entrySet -> {
                    try {
                        File file = new File(String.valueOf(entrySet.getValue()));
                        if(file.exists()){
                            helper.addAttachment(entrySet.getKey(), new FileSystemResource(file));
                        }
                    } catch (MessagingException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            }

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
