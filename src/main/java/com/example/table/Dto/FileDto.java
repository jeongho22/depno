package com.example.table.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class FileDto {
    private Long id;
    private Long employId;
    private String originalName;
    private String saveName;
    private Timestamp createAt;
}
