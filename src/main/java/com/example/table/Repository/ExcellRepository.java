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
public class ExcellRepository {

    private final SqlSessionTemplate sql;

    //1. 엑셀 다운로드
    public List<EmployeeDto> searchEmployeesWithoutPaging(String searchType, String query, String sortOrder, String sortBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchType", searchType);
        params.put("query", "%" + query + "%");
        params.put("sortOrder", sortOrder);
        params.put("sortBy", sortBy);
        return sql.selectList("excel.searchEmployeesWithoutPaging", params);
    }

}
