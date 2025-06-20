package com.austincwang.getup.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    // async annotation to run this on a separate thread
    // lets the calling thread continue execution without waiting
    @Async
    public void sendEmail(String recipient,
                          String username,
                          EmailTemplateName emailTemplate,
                          String confirmationUrl,
                          String activationCode,
                          String subject)
            throws MessagingException
    {
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        }

        else {
            templateName = emailTemplate.name();
        }

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                msg,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> props = new HashMap<>();
        props.put("username", username);
        props.put("confirmationUrl", confirmationUrl);
        props.put("activationCode", activationCode);

        Context context = new Context();
        context.setVariables(props);

        helper.setFrom("contact@getup.com");
        helper.setTo(recipient);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(msg);
    }
}
