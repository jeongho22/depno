package com.example.table.Repository;

import com.example.table.Dto.EmployeeDto;
import com.example.table.Dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {

    private final SqlSessionTemplate sql;

    // params.put: MyBatis에서 SQL 쿼리에 사용할 파라미터를 Map 객체에 저장하기 위해 사용됩니다.
    // 데이터 접근 레이어에서 SQL 쿼리에 파라미터를 전달하는 역할

    // 1. 게시글 리스트 조회
    public List<EmployeeDto> findAllPaged(int offset, int size, String sortOrder,String sortBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);
        params.put("sortOrder", sortOrder);
        params.put("sortBy",sortBy);
        return sql.selectList("employee.findAllPaged", params);
    }

    // 1-1. 전체 게시글 수(Count)
    public int getTotalBoardCount() {
        return sql.selectOne("employee.getTotalBoardCount");
    }

    // 1-2. 검색어 조회
    public List<EmployeeDto> searchEmployees(String searchType, String query, int offset, int size, String sortOrder,String sortBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchType", searchType);
        params.put("query", "%" + query + "%");
        params.put("offset", offset);
        params.put("size", size);
        params.put("sortOrder", sortOrder);
        params.put("sortBy",sortBy);
        return sql.selectList("employee.searchEmployees", params);
    }

    // 1-2. 검색된 게시글 수(Count)
    public int getTotalSearchCount(String searchType, String query) {
        Map<String, String> params = new HashMap<>();
        params.put("searchType", searchType);
        params.put("query", "%" + query + "%");
        return sql.selectOne("employee.getTotalSearchCount", params);
    }

    //2. 직원 등록
    public EmployeeDto save(EmployeeDto employeeDto) {
        sql.insert("employee.save", employeeDto);
        return employeeDto;
    }

    //3. 직원 상세 정보
    public EmployeeDto findById(Long id) {
        return sql.selectOne("employee.findById", id);
    }

    //4. 직원 수정
    public void update(EmployeeDto employeeDto) {
        sql.update("employee.update", employeeDto);
    }

    //5. 직원 삭제
    public void delete(Long id) {
        sql.update("employee.delete", id);
    }


    //6. 직원 번호 중복 확인
    public EmployeeDto findByDeptNo(String deptNo) {
        return sql.selectOne("employee.findByDeptNo", deptNo);
    }


    // 7. 첨부파일 저장
    public void saveFile(FileDto fileDto) {
        sql.insert("employee.saveFile", fileDto);
    }



}
