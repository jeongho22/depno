package com.example.table.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


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
    private int fileAttached; // 첨부파일 여부
    private List<MultipartFile> employFile; // 다중 파일 업로드를 위한 필드
    private List<FileDto> files;        // 업로드된 파일 목록
    private List<Long> filesToDelete;   // 삭제할 파일 ID 목록
    private int emailCount;
}
