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

        EmployeeDto employee = employeeService.findById(employeeId);

        String emailContent =

                        "직원 정보\n" +
                        "\n" +
                        "이름 : " + employee.getEmployeeName() + "\n" +
                        "사번 : " + employee.getDeptNo() + "\n" +
                        "계급 : " + employee.getPosition() + "\n" +
                        "이메일 : " + employee.getEmail() + "\n" +
                        "전화번호 : " + employee.getPhoneNm();


        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail(employee.getEmail());    // 메일 ID
        emailDto.setSubject(employee.getEmployeeName() + "님의 정보 내역 입니다."); // 메일 제목
        emailDto.setText(emailContent);              // 메일 내용
        emailDto.setEmployeeId(employeeId);          // 직원 번호

        emailService.sendEmployeeInfo(emailDto);
        emailService.saveMailHistory(employeeId, employee.getEmail());

        int emailCount = emailService.countMailHistoryByEmployeeId(employeeId);
        emailDto.setEmailCount(emailCount);

        return "메일을 성공적으로 보냈습니다 : " + "총 "+emailCount+"건";
    }
}
