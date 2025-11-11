package com.taskflow.notification_service.business;

import com.taskflow.notification_service.business.dto.TaskDTO;
import com.taskflow.notification_service.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.sender.address}")
    private String senderEmail;

    @Value("${email.sender.name}")
    private String senderName;

    public void sendEmail(TaskDTO taskDTO){
        try {
            // Implementation for sending email using JavaMailSender and Thymeleaf
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(new InternetAddress(senderEmail, senderName));
            mimeMessageHelper.setTo(InternetAddress.parse(taskDTO.getUserEmail()));
            mimeMessageHelper.setSubject("Task Reminder: " + taskDTO.getDescription());

            // Prepare the email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("taskName", taskDTO.getTaskName());
            context.setVariable("eventDate", taskDTO.getEventDate());
            context.setVariable("description", taskDTO.getDescription());
            String template = templateEngine.process("notification", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            // Handle exceptions related to email sending
            throw new EmailException("Failed to send email to " + taskDTO.getUserEmail(), e.getCause());

        }
    }



}
