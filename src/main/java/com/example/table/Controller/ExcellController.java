package com.example.table.Controller;

import com.example.table.Service.ExcellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class ExcellController {

    private final ExcellService excellService;

    // 1. 엑셀 다운로드
    @GetMapping("/downloadExcel")
    @ResponseBody
    public ResponseEntity<byte[]> downloadExcel(@RequestParam String searchType, @RequestParam String query,
                                                @RequestParam String sortOrder, @RequestParam String sortBy) throws IOException {
        return excellService.downloadExcel(searchType, query, sortOrder, sortBy);
    }

    //2. 엑셀 업로드
    @PostMapping("/uploadExcel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadExcel(@RequestParam("uploadFile") MultipartFile file) throws IOException {
        Map<String, Object> result = excellService.uploadExcel(file);
        return ResponseEntity.ok(result);
    }

}
