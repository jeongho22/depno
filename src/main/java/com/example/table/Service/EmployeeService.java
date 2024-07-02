package com.example.table.Service;

import com.example.table.Dto.EmployeeDto;
import com.example.table.Dto.FileDto;
import com.example.table.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    // 정규표현식 패턴 정의
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zㄱ-힣]{1,10}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
    private static final Pattern DEPT_NO_PATTERN = Pattern.compile("^[0-9]{3}$");

    //1. 직원 리스트 조회
    public List<EmployeeDto> findAllPaged(int page, int size, String sortOrder, String sortBy) {
        int offset = (page - 1) * size;
        return employeeRepository.findAllPaged(offset, size, sortOrder, sortBy);
    }

    //1-1. 리스트 데이터 갯수
    public int getTotalBoardCount() {
        return employeeRepository.getTotalBoardCount();
    }

    //1-2. 검색어 조회
    public List<EmployeeDto> searchPaged(String searchType, String query, int page, int size, String sortOrder, String sortBy) {
        int offset = (page - 1) * size;
        return employeeRepository.searchEmployees(searchType, query, offset, size, sortOrder, sortBy);
    }
    //1-2. 검색어 데이터 갯수
    public int getTotalSearchCount(String searchType, String query) {
        return employeeRepository.getTotalSearchCount(searchType, query);
    }



    //2. 직원 등록
    public void save(EmployeeDto employeeDto) throws IOException {

        // 중복 확인 로직 추가
        if (employeeRepository.findByDeptNo(employeeDto.getDeptNo()) != null) {
            throw new IllegalArgumentException("이미 존재하는 직원 번호입니다.");
        }
        if (!NAME_PATTERN.matcher(employeeDto.getEmployeeName()).matches()) {
            throw new IllegalArgumentException("유효한 이름을 입력하세요. (한글 또는 영문, 1자 이상 10자 이하)");
        }
        if (employeeDto.getDeptNo() == null || employeeDto.getDeptNo().trim().isEmpty()) {
            throw new IllegalArgumentException("직원 번호는 공백 일 수 없습니다.");
        }
        if (!DEPT_NO_PATTERN.matcher(employeeDto.getDeptNo()).matches()) {
            throw new IllegalArgumentException("직원 번호는 3자리 숫자여야 합니다.");
        }
        if (employeeDto.getPosition() == null || employeeDto.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("직급은 공백 일 수 없습니다.");
        }
        if (!PHONE_PATTERN.matcher(employeeDto.getPhoneNm()).matches()) {
            throw new IllegalArgumentException("유효한 핸드폰 번호를 입력하세요. (예: 010-1234-5678)");
        }
        if (!EMAIL_PATTERN.matcher(employeeDto.getEmail()).matches()) {
            throw new IllegalArgumentException("유효한 이메일을 입력하세요. (예: example@domain.com)");
        }

        employeeDto.setCreatedAt(LocalDateTime.now());
        employeeRepository.save(employeeDto);
        handleFileUpload(employeeDto);

        // fileAttached 값 업데이트
        if (employeeDto.getFileAttached() == 1) {
            employeeRepository.updateFileAttached(employeeDto.getId(), employeeDto.getFileAttached());
        }

    }


    //3. 직원 상세 정보
    public EmployeeDto findById(Long id) {
        EmployeeDto employee = employeeRepository.findById(id);
        if (employee != null) {
            List<FileDto> files = employeeRepository.findFilesByEmployeeId(id);
            if (files != null && !files.isEmpty()) {
                employee.setFiles(files);
                employee.setFileAttached(1);  // 파일이 있는 경우 fileAttached 필드를 1로 설정
            } else {
                employee.setFileAttached(0);  // 파일이 없는 경우 fileAttached 필드를 0으로 설정
            }
        }
        return employee;
    }


    //4. 직원 수정
    public void update(EmployeeDto employeeDto) throws IOException {

        EmployeeDto existingEmployee = employeeRepository.findByDeptNo(employeeDto.getDeptNo());
        if (existingEmployee != null && !existingEmployee.getId().equals(employeeDto.getId())) {
            throw new IllegalArgumentException("이미 존재하는 직원 번호입니다.");
        }
        if (!NAME_PATTERN.matcher(employeeDto.getEmployeeName()).matches()) {
            throw new IllegalArgumentException("유효한 이름을 입력하세요. (한글 또는 영문, 1자 이상 10자 이하)");
        }
        if (employeeDto.getDeptNo() == null || employeeDto.getDeptNo().trim().isEmpty()) {
            throw new IllegalArgumentException("직원 번호는 공백 일 수 없습니다.");
        }
        if (!DEPT_NO_PATTERN.matcher(employeeDto.getDeptNo()).matches()) {
            throw new IllegalArgumentException("직원 번호는 3자리 숫자여야 합니다.");
        }
        if (employeeDto.getPosition() == null || employeeDto.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("직급은 공백 일 수 없습니다.");
        }
        if (!PHONE_PATTERN.matcher(employeeDto.getPhoneNm()).matches()) {
            throw new IllegalArgumentException("유효한 핸드폰 번호를 입력하세요. (예: 010-1234-5678)");
        }
        if (!EMAIL_PATTERN.matcher(employeeDto.getEmail()).matches()) {
            throw new IllegalArgumentException("유효한 이메일을 입력하세요. (예: example@domain.com)");
        }

        employeeDto.setModifiedAt(LocalDateTime.now());


        // 처리 전에 기존 파일을 모두 삭제하고 업데이트 로직을 수행
        if (employeeDto.getFilesToDelete() != null) {
            for (Long fileId : employeeDto.getFilesToDelete()) {
                FileDto fileDto = employeeRepository.findFileById(fileId);

                if (fileDto != null) {
                    File file = new File("C:/Users/USER/Desktop/uploading/" + fileDto.getSaveName());
                    if (file.exists()) {
                        file.delete();
                    }
                    employeeRepository.deleteFile(fileId);
                }
            }
        }
        handleFileUpload(employeeDto);


        // 파일 삭제 후 남아 있는 파일 확인하여 fileAttached 업데이트
        List<FileDto> remainingFiles = employeeRepository.findFilesByEmployeeId(employeeDto.getId());
        if (remainingFiles == null || remainingFiles.isEmpty()) {
            employeeDto.setFileAttached(0);
            employeeRepository.updateFileAttached(employeeDto.getId(), 0);
        } else {
            employeeRepository.updateFileAttached(employeeDto.getId(), 1);
        }
        employeeRepository.update(employeeDto);
    }


    //5. 직원 삭제
    public void delete(List<Long> ids) throws IOException {
        for (Long id : ids) {
            EmployeeDto employee = employeeRepository.findById(id);
            if (employee != null && employee.getFileAttached() == 1) {
                List<FileDto> files = employeeRepository.findFilesByEmployeeId(id);
                for (FileDto file : files) {
                    File fileToDelete = new File("C:/Users/USER/Desktop/uploading/" + file.getSaveName());
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                    employeeRepository.deleteFile(file.getId());
                }
            }
            employeeRepository.delete(id);
        }
    }



    //6. 직원 번호 중복 확인
    public EmployeeDto findByDeptNo(String deptNo) {
        return employeeRepository.findByDeptNo(deptNo);
    }



    //7. 파일 업로드 핸들러
    private void handleFileUpload(EmployeeDto employeeDto) throws IOException {
        boolean hasFiles = false;
        if (employeeDto.getEmployFile() != null && !employeeDto.getEmployFile().isEmpty()) {
            for (MultipartFile employFile : employeeDto.getEmployFile()) {
                if (!employFile.isEmpty()) {  // 파일이 비어있지 않은 경우에만 처리
                    hasFiles = true;
                    String originalFilename = employFile.getOriginalFilename();
                    String saveFileName = UUID.randomUUID().toString() + getExtension(originalFilename);

                    FileDto fileDto = new FileDto();
                    fileDto.setOriginalName(originalFilename);
                    fileDto.setSaveName(saveFileName);
                    fileDto.setEmployId(employeeDto.getId());

                    String savePath = "C:/Users/USER/Desktop/uploading/" + saveFileName;

                    File destFile = new File(savePath);
                    employFile.transferTo(destFile);

                    employeeRepository.saveFile(fileDto);
                }
            }
        }
        if (!hasFiles) {
            employeeDto.setFileAttached(0);
        } else {
            employeeDto.setFileAttached(1);
        }
    }


    // 8. 파일 경로의 확장명 (txt ,jpg, docx)
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }

}
