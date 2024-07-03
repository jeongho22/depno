package com.example.table.Controller;

import com.example.table.Dto.EmailDto;
import com.example.table.Dto.EmployeeDto;
import com.example.table.Service.EmailService;
import com.example.table.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EmailController {
    private final EmployeeService employeeService;
    private final EmailService emailService;

    @PostMapping("/send-email")
    @ResponseBody
    public String sendEmail(@RequestParam Long employeeId) {
        EmployeeDto employee = employeeService.findById(employeeId);


        String emailContent = "Employee Information\n" +
                "Name: " + employee.getEmployeeName() + "\n" +
                "Position: " + employee.getPosition() + "\n" +
                "Email: " + employee.getEmail() + "\n" +
                "Phone: " + employee.getPhoneNm();

        EmailDto emailDto = new EmailDto();
        emailDto.setToEmail(employee.getEmail());
        emailDto.setSubject("Employee Information");
        emailDto.setText(emailContent);
        emailDto.setEmployeeId(employeeId);

        emailService.sendEmployeeInfo(emailDto);
        emailService.saveMailHistory(employeeId, employee.getEmail());

        int emailCount = emailService.countMailHistoryByEmployeeId(employeeId);
        emailDto.setEmailCount(emailCount);

        return "메일을 성공적으로 보냈습니다 : " + emailCount;
    }
}
