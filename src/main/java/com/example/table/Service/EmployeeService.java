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
import java.util.ArrayList;
import java.util.List;
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

        if (employeeDto.getEmployFile().get(0).isEmpty()) {
            // 파일 없다.
            employeeDto.setFileAttached(0);
            employeeRepository.save(employeeDto);
        } else {
            // 파일 있다.
            employeeDto.setFileAttached(1);
            // 게시글 저장 후 id값 활용을 위해 리턴 받음.
            EmployeeDto savedEmploy = employeeRepository.save(employeeDto);
            // 파일만 따로 가져오기
            for (MultipartFile employFile: employeeDto.getEmployFile()) {
                // 파일 이름 가져오기
                String originalFilename = employFile.getOriginalFilename();
                System.out.println("originalFilename = " + originalFilename);
                // 저장용 이름 만들기
                System.out.println(System.currentTimeMillis());
                String storedFileName = System.currentTimeMillis() + "-" + originalFilename;
                System.out.println("storedFileName = " + storedFileName);

                // BoardFileDTO 세팅
                FileDto fileDto = new FileDto();
                fileDto.setOriginalName(originalFilename);
                fileDto.setSaveName(storedFileName);
                fileDto.setEmployId(savedEmploy.getId());

                // 파일 저장용 폴더에 파일 저장 처리
                String savePath = "C:/Users/USER/Desktop/uploading/" + storedFileName;

                employFile.transferTo(new File(savePath));
                // board_file_table 저장 처리
                employeeRepository.saveFile(fileDto);
            }
        }
    }

    //3. 직원 상세 정보
    public EmployeeDto findById(Long id) {
        return employeeRepository.findById(id);
    }


    //4. 직원 수정
    public void update(EmployeeDto employeeDto) {

        // 중복 확인 로직 추가
        EmployeeDto existingEmployee = employeeRepository.findByDeptNo(employeeDto.getDeptNo());
        if (existingEmployee != null && !existingEmployee.getId().equals(employeeDto.getId())) {
            throw new IllegalArgumentException("이미 존재하는 직원 번호입니다.");
        }
        if (!NAME_PATTERN.matcher(employeeDto.getEmployeeName()).matches()) {
            throw new IllegalArgumentException("유효한 이름을 입력하세요. (한글 또는 영문, 1자 이상 10자 이하)");
        }
        if (employeeDto.getDeptNo() == null || employeeDto.getDeptNo().trim().isEmpty()) {
            throw new IllegalArgumentException("직원 번호는 공백 일수 없습니다.");
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
        employeeRepository.update(employeeDto);
    }

    //5. 직원 삭제
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            employeeRepository.delete(id);
        }
    }

    //6. 직원 번호 중복 확인
    public EmployeeDto findByDeptNo(String deptNo) {
        return employeeRepository.findByDeptNo(deptNo);
    }


}
