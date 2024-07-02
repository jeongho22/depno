package com.example.table.Controller;
import com.example.table.Dto.EmployeeDto;
import com.example.table.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;


    //1. 직원 리스트 조회 (/list?page=2&size=20)
    @GetMapping("/list")
    public String findAllOrSearch(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            Model model) {

        List<EmployeeDto> employeeList;
        int totalBoardCount;

        if (searchType != null && query != null && !query.isEmpty()) {
            // 검색 조건이 있는 경우
            employeeList = employeeService.searchPaged(searchType, query, page, size, sortOrder, sortBy);
            totalBoardCount = employeeService.getTotalSearchCount(searchType, query);
        } else {
            // 검색 조건이 없는 경우
            employeeList = employeeService.findAllPaged(page, size, sortOrder, sortBy);
            totalBoardCount = employeeService.getTotalBoardCount();
        }

        int totalPages = (int) Math.ceil((double) totalBoardCount / size);


        model.addAttribute("searchType", searchType);
        model.addAttribute("query", query);
        model.addAttribute("size", size);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("EmployeeList", employeeList);
        model.addAttribute("totalBoardCount", totalBoardCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);


//        System.out.println("검색 및 정렬 기준: " + sortBy + ", 정렬 순서: " + sortOrder);
//        System.out.println("검색 유형:" + searchType);
//        System.out.println("검색어:" + query);
//        System.out.println("최대 몇개씩? 보여 줄까?:" + size);
//        System.out.println("현재 페이지 데이터 리스트:" + employeeList);
//        System.out.println("총 데이터 개수:" + totalBoardCount);
//        System.out.println("전체 페이지 개수 :" + totalPages);

        return "main";
    }

    //2. 직원 등록
    @PostMapping("/save")
    public String save(@ModelAttribute EmployeeDto employeeDto) throws IOException {
        employeeService.save(employeeDto);
        return "redirect:/list";
    }


    //3. 직원 상세 정보
    @GetMapping("/detail/{id}")
    @ResponseBody
    public EmployeeDto getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.findById(id);
        System.out.println(employee);
        return employee;
    }


    //4. 직원 수정
    @PostMapping("/update/{id}")
    public String update(@ModelAttribute EmployeeDto employeeDto) throws IOException {
        employeeService.update(employeeDto);
        return "redirect:/list";
    }


    //5. 직원 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam("ids") List<Long> ids) throws IOException {
        employeeService.delete(ids);
        return "redirect:/list";
    }

    //6. 직원 번호 중복 확인
    @GetMapping("/check-duplicate")
    @ResponseBody
    public boolean checkDuplicate(@RequestParam String deptNo) {
        return employeeService.findByDeptNo(deptNo) != null;
    }


    //7. 파일 다운로드
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, @RequestParam String originalName) {
        try {
            // 1. 업로드 된 파일이 해당 경로
            File file = new File("C:/Users/USER/Desktop/uploading/" + fileName);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Resource resource = new FileSystemResource(file);
            String encodedFileName = URLEncoder.encode(originalName, StandardCharsets.UTF_8);
            encodedFileName = encodedFileName.replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
