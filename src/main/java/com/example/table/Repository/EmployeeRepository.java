package com.example.table.Repository;

import com.example.table.Dto.EmployeeDto;
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

    // 1. 게시글 리스트 조회
    public List<EmployeeDto> findAllPaged(int offset, int size) {
        Map<String, Integer> params = new HashMap<>(); // 페이징 파라미터를 담을 맵 생성
        params.put("offset", offset); // 오프셋 설정
        params.put("limit", size); // 페이지 크기 설정
        return sql.selectList("employee.findAllPaged", params); // MyBatis 매퍼의 "employee.findAllPaged" 쿼리를 실행하여 게시글 목록 조회
    }

    // 전체 게시글 수를 가져오는 메서드
    public int getTotalBoardCount() {
        return sql.selectOne("employee.getTotalBoardCount"); // MyBatis 매퍼의 "employee.getTotalBoardCount" 쿼리를 실행하여 전체 게시글 수 조회
    }

    // 1-1. 검색어 조회
    public List<EmployeeDto> searchEmployees(String searchType, String query, int offset, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchType", searchType);
        params.put("query", "%" + query + "%");
        params.put("offset", offset);
        params.put("limit", size);
        return sql.selectList("employee.searchEmployees", params);
    }

    public int getTotalSearchCount(String searchType, String query) {
        Map<String, String> params = new HashMap<>();
        params.put("searchType", searchType);
        params.put("query", "%" + query + "%");
        return sql.selectOne("employee.getTotalSearchCount", params);
    }

    //2. 직원 등록
    public void save(EmployeeDto employeeDto) {
        sql.insert("employee.save", employeeDto);
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


    // 6. 직원 번호로 직원 조회
    public EmployeeDto findByDeptNo(String deptNo) {
        return sql.selectOne("employee.findByDeptNo", deptNo);
    }

}
