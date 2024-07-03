package com.example.table.Repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmailRepository {

    private final SqlSessionTemplate sql;

    public void saveMailHistory(Long employeeId, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("email", email);
        sql.insert("email.saveMailHistory", params);
    }

    public int countMailHistoryByEmployeeId(Long employeeId) {
        return sql.selectOne("email.countMailHistoryByEmployeeId", employeeId);
    }
}
