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

    //1. 메일 저장 (DB)
    public void saveMailHistory(Long employeeId, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", employeeId);
        params.put("email", email);
        sql.insert("email.saveMailHistory", params);
    }

    //2. 이메일 보낸 횟수
    public int countMailHistoryByEmployeeId(Long employeeId) {
        return sql.selectOne("email.countMailHistoryByEmployeeId", employeeId);
    }
}
