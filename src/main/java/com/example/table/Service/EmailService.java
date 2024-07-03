package com.example.table.Service;

import com.example.table.Dto.EmailDto;
import com.example.table.Repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmployeeInfo(EmailDto emailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailDto.getToEmail());
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getText());
        mailSender.send(message);
    }

    public void saveMailHistory(Long employeeId, String email) {
        emailRepository.saveMailHistory(employeeId, email);
    }

    public int countMailHistoryByEmployeeId(Long employeeId) {
        return emailRepository.countMailHistoryByEmployeeId(employeeId);
    }
}
