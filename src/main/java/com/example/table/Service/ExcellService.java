package com.example.table.Service;
import com.example.table.Dto.EmployeeDto;
import com.example.table.Dto.FileDto;
import com.example.table.Repository.EmailRepository;
import com.example.table.Repository.EmployeeRepository;
import com.example.table.Repository.ExcellRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class ExcellService {



    private final EmployeeRepository employeeRepository;
    private final ExcellRepository excellRepository;
    private final EmailRepository emailRepository;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");


    // 1. 엑셀 다운로드
    public ResponseEntity<byte[]> downloadExcel(String searchType, String query, String sortOrder, String sortBy) throws IOException {

        // 테이블 리스트
        List<EmployeeDto> employeeList = excellRepository.searchEmployeesWithoutPaging(searchType, query, sortOrder, sortBy);

        for (EmployeeDto employee : employeeList) {
            // file 첨부 내역
            List<FileDto> files = employeeRepository.findFilesByEmployeeId(employee.getId());
            employee.setFiles(files);

            // 이메일 발송 횟수 설정
            int emailCount = emailRepository.countMailHistoryByEmployeeId(employee.getId());
            employee.setEmailCount(emailCount);
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"직원번호", "직원명", "직급", "전화번호", "이메일", "등록일", "수정일", "첨부파일 내역", "이메일 발송 횟수"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 데이터 행 생성
        int rowNum = 1;
        for (EmployeeDto employee : employeeList) {
            Row row = sheet.createRow(rowNum++);

            // Null 값 처리
            String deptNo = employee.getDeptNo() != null ? employee.getDeptNo() : "";
            String employeeName = employee.getEmployeeName() != null ? employee.getEmployeeName() : "";
            String position = employee.getPosition() != null ? employee.getPosition() : "";
            String phoneNm = employee.getPhoneNm() != null ? employee.getPhoneNm() : "";
            String email = employee.getEmail() != null ? employee.getEmail() : "";
            String createdAt = employee.getCreatedAt() != null ? employee.getCreatedAt().toString() : "";
            String modifiedAt = employee.getModifiedAt() != null ? employee.getModifiedAt().toString() : "";
            List<FileDto> fileList = employee.getFiles() != null ? employee.getFiles() : new ArrayList<>();
            int emailCount = employee.getEmailCount();

            row.createCell(0).setCellValue(deptNo);
            row.createCell(1).setCellValue(employeeName);
            row.createCell(2).setCellValue(position);
            row.createCell(3).setCellValue(phoneNm);
            row.createCell(4).setCellValue(email);
            row.createCell(5).setCellValue(createdAt);
            row.createCell(6).setCellValue(modifiedAt);

            // 첨부 파일 내역 추가
            List<String> originalNames = new ArrayList<>();
            for (FileDto fileDto : fileList) {
                originalNames.add(fileDto.getOriginalName() != null ? fileDto.getOriginalName() : "");
            }
            String fileAttachments = String.join(", ", originalNames);
            row.createCell(7).setCellValue(fileAttachments);

            // 이메일 발송 횟수 추가
            row.createCell(8).setCellValue(emailCount);
        }

        // 열 크기 자동 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] excelBytes = bos.toByteArray();

        String encodedFileName = URLEncoder.encode("직원목록-" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok().headers(responseHeaders).body(excelBytes);
    }


    //2. 엑셀 업로드
    public Map<String, Object> uploadExcel(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (InputStream is = file.getInputStream()) {

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;  // 첫 번째 행은 헤더이므로 건너뜁니다.

                EmployeeDto employee = new EmployeeDto();
                String errorMessage = "";

                // 각 셀의 값을 읽어옵니다.
                employee.setDeptNo(getCellValueAsString(row.getCell(0)));
                employee.setEmployeeName(getCellValueAsString(row.getCell(1)));
                employee.setPosition(getCellValueAsString(row.getCell(2)));
                employee.setPhoneNm(getCellValueAsString(row.getCell(3)));
                employee.setEmail(getCellValueAsString(row.getCell(4)));

                // 유효성 검사
                if (employee.getDeptNo() == null || employee.getDeptNo().trim().isEmpty()) {
                    errorMessage = "직원코드는 공백 일 수 없습니다.";
                } else if (employee.getEmployeeName() == null || employee.getEmployeeName().trim().isEmpty()) {
                    errorMessage = "직원명은 공백 일 수 없습니다.";
                } else if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
                    errorMessage = "직급은 공백 일 수 없습니다.";
                } else if (employeeRepository.findByDeptNo(employee.getDeptNo()) != null) {
                    errorMessage = "이미 존재하는 직원 번호입니다.";
                } else if (!PHONE_PATTERN.matcher(employee.getPhoneNm()).matches()) {
                    errorMessage = "유효한 핸드폰 번호를 입력하세요. (예: 010-1234-5678)";
                } else if (!EMAIL_PATTERN.matcher(employee.getEmail()).matches()) {
                    errorMessage = "유효한 이메일을 입력하세요. (예: example@domain.com)";
                }

                // 유효성 에러 메세지 반환
                if (!errorMessage.isEmpty()) {
                    failCount++;
                    errorMessages.add("Row " + (row.getRowNum()) + ": " + errorMessage);
                    continue;
                }

                // 성공된 데이터 저장
                try {
                    employeeRepository.save(employee);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("Row " + (row.getRowNum()) + ": 데이터베이스 오류");
                }
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);
        return result;
    }

    // 3. 셀의 타입을 확인하여 값을 문자열로 반환
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }




}
