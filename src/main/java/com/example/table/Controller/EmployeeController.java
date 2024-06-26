package com.example.table.Controller;

import com.example.table.Dto.EmployeeDto;
import com.example.table.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;


    // 1. 조회
//    @GetMapping("/list")
//    public String findAll(@RequestParam(defaultValue = "1") int page, Model model) {
//        List<EmployeeDto> EmployeeList = employeeService.findAllPaged(page, 10);
//        int totalBoardCount = employeeService.getTotalBoardCount();
//        int totalPages = (int) Math.ceil((double) totalBoardCount / 10);
//
//        model.addAttribute("EmployeeList", EmployeeList);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//
//        System.out.println(EmployeeList.toString()); // List 내용 출력
//
//        return "main";
//    }

    //1-1. 검색어 조회

    // 1. 조회 및 검색
    @GetMapping("/list")
    public String findAllOrSearch(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String query,
            Model model) {

        int size = 10;  // 페이지 당 항목 수
        List<EmployeeDto> employeeList;
        int totalBoardCount;

        if (searchType != null && query != null && !query.isEmpty()) {
            // 검색 조건이 있는 경우
            employeeList = employeeService.searchEmployees(searchType, query, page, size);
            totalBoardCount = employeeService.getTotalSearchCount(searchType, query);
            model.addAttribute("searchType", searchType);
            model.addAttribute("query", query);
        } else {
            // 검색 조건이 없는 경우
            employeeList = employeeService.findAllPaged(page, size);
            totalBoardCount = employeeService.getTotalBoardCount();
        }

        int totalPages = (int) Math.ceil((double) totalBoardCount / size);
        model.addAttribute("EmployeeList", employeeList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "main";
    }


    //2. 생성
    @PostMapping("/save")
    public String save(@ModelAttribute EmployeeDto employeeDto) throws IOException {
        employeeService.save(employeeDto);
        return "redirect:/list";
    }

    //3. 직원 상세 정보
    @GetMapping("/detail/{id}")
    @ResponseBody
    public EmployeeDto getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    //4. 직원 수정
    @PostMapping("/update/{id}")
    public String update(@ModelAttribute EmployeeDto employeeDto){
        employeeService.update(employeeDto);
        return "redirect:/list";
    }


    //5. 직원 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam("ids") List<Long> ids) {
        employeeService.delete(ids);
        return "redirect:/list";
    }

    //6. 직원 중복 확인
    @GetMapping("/check-duplicate")
    @ResponseBody
    public boolean checkDuplicate(@RequestParam String deptNo) {
        return employeeService.findByDeptNo(deptNo) != null;
    }


}
