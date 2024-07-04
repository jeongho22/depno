package com.example.table.Controller;
import com.example.table.Dto.EmailDto;
import com.example.table.Dto.EmployeeDto;
import com.example.table.Dto.FileDto;
import com.example.table.Service.EmailService;
import com.example.table.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class EmailController {
    private final EmployeeService employeeService;
    private final EmailService emailService;

    // 1. 메일 발송
    @PostMapping("/send-email")
    @ResponseBody
    public String sendEmail(@RequestParam Long employeeId) {

        EmployeeDto employeeDto = employeeService.findById(employeeId);  // 메일 보낼 특정 대상 가져 오기

        // employeeDto.getFiles()가 반환하는 파일 리스트에서 originalName만 추출
        List<FileDto> fileList = employeeDto.getFiles();

        // originalName를 저장할 리스트 새성
        List<String> originalNames = new ArrayList<>();

        // 첨부 파일 반복문 돌려서 다 가져 오기
        for (FileDto fileDto : fileList) {
            originalNames.add(fileDto.getOriginalName());
        }

        // 첨부 파일 내역 (첨부1, 첨부2, 첨부3) 콤마로 나눠줌
        String fileAttachments = String.join(", ", originalNames);

        String emailContent = "직원 정보\n" +
                "\n" +
                "이름 : " + employeeDto.getEmployeeName() + "\n" +
                "사번 : " + employeeDto.getDeptNo() + "\n" +
                "계급 : " + employeeDto.getPosition() + "\n" +
                "이메일 : " + employeeDto.getEmail() + "\n" +
                "전화번호 : " + employeeDto.getPhoneNm() + "\n" +
                "파일 첨부 내역 : " + fileAttachments;

        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail(employeeDto.getEmail());    // 메일 ID
        emailDto.setSubject(employeeDto.getEmployeeName() + "님의 정보 내역 입니다."); // 메일 제목
        emailDto.setText(emailContent);              // 메일 내용
        emailDto.setEmployeeId(employeeId);          // 직원 번호

        emailService.sendEmployeeInfo(emailDto);     // 메일 내용 보내기
        emailService.saveMailHistory(employeeId, employeeDto.getEmail());   // DB 저장

        int emailCount = emailService.countMailHistoryByEmployeeId(employeeId); // 메일 갯수
        emailDto.setEmailCount(emailCount);

        return "메일을 성공적으로 보냈습니다 : " + "총 " + emailCount + "건";
    }
}
