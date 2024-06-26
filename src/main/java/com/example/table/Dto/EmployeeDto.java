package com.example.table.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
public class EmployeeDto {
    private Long id;
    private String deptNo;  // 기존 DEPT_NO에서 변경
    private String employeeName;  // 기존 Employee_name에서 변경
    private String position;  // 기존 POSITION에서 변경
    private String email;  // 기존 EMAIL에서 변경
    private String phoneNm;  // 기존 Phone_NM에서 변경
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
