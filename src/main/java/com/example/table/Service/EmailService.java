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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

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

    // 4. 서버 정보 이메일 보내기
    @Scheduled(cron = "0 0 14 * * ?")
    public void reportServerStatus() {
        try {
            // OS 정보
            String os = System.getProperty("os.name");

            // IP 주소 및 MAC 주소 정보
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] macArray = network.getHardwareAddress();
            StringBuilder mac = new StringBuilder();
            for (int i = 0; i < macArray.length; i++) {
                mac.append(String.format("%02X%s", macArray[i], (i < macArray.length - 1) ? "-" : ""));
            }

            // CPU 및 메모리 사용량
            com.sun.management.OperatingSystemMXBean osBean =
                    (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuLoad = osBean.getSystemCpuLoad() * 100;
            long freeMemory = osBean.getFreePhysicalMemorySize();
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long usedMemory = totalMemory - freeMemory;

            // 이메일 내용 작성
            String emailContent = String.format(
                    "OS: %s\nIP Address: %s\nMAC Address: %s\nCPU Load: %.2f%%\nMemory Usage: %d/%d (%.2f%%)",
                    os, ip.getHostAddress(), mac.toString(), cpuLoad, usedMemory, totalMemory, (double) usedMemory / totalMemory * 100
            );

            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("jhjh1919jh@gmail.com"); // 수신자 이메일 주소
            message.setSubject("서버 현황 리포트");
            message.setText(emailContent);

            mailSender.send(message);

            System.out.println("Server status report sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
