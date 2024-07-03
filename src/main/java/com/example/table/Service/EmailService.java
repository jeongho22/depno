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


    // 1. 메일 발송 내역
    public void sendEmployeeInfo(EmailDto emailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);                // 보내는 사람
        message.setTo(emailDto.getToEmail());      // 받는 사람
        message.setSubject(emailDto.getSubject()); // 제목
        message.setText(emailDto.getText());       // 내용
        mailSender.send(message);                  // 보내기
    }

    //2. 메일 저장 (DB)
    public void saveMailHistory(Long employeeId, String email) {
        emailRepository.saveMailHistory(employeeId, email);
    }


    //3. 이메일 보낸 횟수
    public int countMailHistoryByEmployeeId(Long employeeId) {
        return emailRepository.countMailHistoryByEmployeeId(employeeId);
    }
}
