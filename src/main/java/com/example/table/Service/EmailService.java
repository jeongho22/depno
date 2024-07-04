package com.example.table.Service;

import com.example.table.Dto.EmailDto;
import com.example.table.Dto.FileDto;
import com.example.table.Repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;


//    // 1. 메일 발송 내역
//    public void sendEmployeeInfo(EmailDto emailDto) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);                // 보내는 사람
//        message.setTo(emailDto.getToEmail());      // 받는 사람
//        message.setSubject(emailDto.getSubject()); // 제목
//        message.setText(emailDto.getText());       // 내용
//        mailSender.send(message);                  // 보내기
//    }

    // 1. 메일 발송 내역
    private static final String FIXED_FILE_PATH = "C:/Users/USER/Desktop/uploading/";  // 내 로컬에서 파일이 저장된 고정 경로 (자기 환경에 맞게 수정)
    public void sendEmployeeInfo(EmailDto emailDto, List<FileDto> files) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // 첨부파일 기능

        helper.setFrom(fromEmail);                // 보내는 사람
        helper.setTo(emailDto.getToEmail());      // 받는 사람
        helper.setSubject(emailDto.getSubject()); // 제목
        helper.setText(emailDto.getText(), true); // 내용 (HTML 가능)

        // 첨부파일 추가
        if (files != null) {
            for (FileDto fileDto : files) {
                File file = new File(FIXED_FILE_PATH + fileDto.getSaveName()); // 고정 경로와 저장된 파일명을 결합하여 File 객체 생성
                helper.addAttachment(fileDto.getOriginalName(), file); // 첨부파일 추가
            }
        }
        mailSender.send(message);                  // 보내기
    }

    //2. 보낸 메일 저장 (DB)
    public void saveMailHistory(Long employeeId, String email) {
        emailRepository.saveMailHistory(employeeId, email);
    }


    //3. 특정 직원 에게 이메일 보낸 횟수
    public int countMailHistoryByEmployeeId(Long employeeId) {
        return emailRepository.countMailHistoryByEmployeeId(employeeId);
    }
}
