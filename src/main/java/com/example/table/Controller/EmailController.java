package com.example.table.Controller;
import com.example.table.Dto.EmailDto;
import com.example.table.Dto.EmployeeDto;
import com.example.table.Service.EmailService;
import com.example.table.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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

        String emailContent =

                        "직원 정보\n" +
                        "\n" +
                        "이름 : " + employeeDto.getEmployeeName() + "\n" +
                        "사번 : " + employeeDto.getDeptNo() + "\n" +
                        "계급 : " + employeeDto.getPosition() + "\n" +
                        "이메일 : " + employeeDto.getEmail() + "\n" +
                        "전화번호 : " + employeeDto.getPhoneNm();


        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail(employeeDto.getEmail());    // 메일 ID
        emailDto.setSubject(employeeDto.getEmployeeName() + "님의 정보 내역 입니다."); // 메일 제목
        emailDto.setText(emailContent);              // 메일 내용
        emailDto.setEmployeeId(employeeId);          // 직원 번호


        emailService.sendEmployeeInfo(emailDto);     // 메일 내용 보내기

        emailService.saveMailHistory(employeeId, employeeDto.getEmail());   // DB 저장

        int emailCount = emailService.countMailHistoryByEmployeeId(employeeId); // 메일 갯수
        emailDto.setEmailCount(emailCount);

        return "메일을 성공적으로 보냈습니다 : " + "총 "+emailCount+"건";
    }
}
