package com.example.table.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmailDto {
    private String toEmail;
    private String subject;
    private String text;
    private Long employeeId;
    private String file;
    private int emailCount;
}
