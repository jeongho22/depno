<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Employee List</title>
    <!-- Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">



    <div class="d-flex mb-3">
        <!-- Buttons -->
        <button type="button" class="btn btn-primary mr-2" data-toggle="modal" data-target="#registerModal">등록</button>
        <button type="button" class="btn btn-secondary mr-2" onclick="openUpdateModal()">수정</button>
        <button type="button" class="btn btn-danger mr-2" onclick="deleteEmployee()">삭제</button>
        <button type="button" class="btn btn-info mr-2" onclick="window.location.href='${pageContext.request.contextPath}/list'">목록</button>



        <div>
            <select id="itemsPerPage" onchange="changeItemsPerPage()" class="form-control">
                <option value="5" ${size == 5 ? 'selected' : ''}>5개씩</option>
                <option value="10" ${size == 10 ? 'selected' : ''}>10개씩</option>
                <option value="15" ${size == 15 ? 'selected' : ''}>15개씩</option>
                <option value="20" ${size == 20 ? 'selected' : ''}>20개씩</option>
                <option value="50" ${size == 50 ? 'selected' : ''}>50개씩</option>
            </select>
        </div>


        <!-- Sort by field -->
        <div class="form-group mr-2">
            <select id="sortBy" name="sortBy" class="form-control" onchange="sortData()">
                <option value="deptNo" ${sortBy == 'deptNo' ? 'selected' : ''}>직원번호</option>
                <option value="createdAt" ${sortBy == 'createdAt' ? 'selected' : ''}>생성일</option>
                <option value="modifiedAt" ${sortBy == 'modifiedAt' ? 'selected' : ''}>수정일</option>
            </select>
        </div>

        <!-- Sort order -->
        <div class="form-group mr-2">
            <select id="sortOrder" name="sortOrder" class="form-control" onchange="sortData()">
                <option value="asc" ${sortOrder == 'asc' ? 'selected' : ''}>오름차순</option>
                <option value="desc" ${sortOrder == 'desc' ? 'selected' : ''}>내림차순</option>
            </select>
        </div>


        <!-- 검색 폼 -->
        <form id="searchForm" method="get" action="${pageContext.request.contextPath}/list" class="form-inline ml-auto">
            <div class="form-group mr-2">
                <select id="searchType" name="searchType" class="form-control">
                    <option value="all" ${searchType == 'all' ? 'selected' : ''}>전체</option>
                    <option value="deptNo" ${searchType == 'deptNo' ? 'selected' : ''}>직원번호</option>
                    <option value="employeeName" ${searchType == 'employeeName' ? 'selected' : ''}>직원명</option>
                    <option value="position" ${searchType == 'position' ? 'selected' : ''}>직급</option>
                    <option value="email" ${searchType == 'email' ? 'selected' : ''}>이메일</option>
                    <option value="modifiedAt" ${searchType == 'modifiedAt' ? 'selected' : ''}>수정일시</option>
                </select>
            </div>
            <div class="form-group mr-2">
                <input type="text" id="query" name="query" class="form-control" placeholder="검색어를 입력하세요" value="${query}">
            </div>
            <input type="hidden" id="sortOrderHidden" name="sortOrder" value="${sortOrder}">
            <input type="hidden" id="sortByHidden" name="sortBy" value="${sortBy}">
            <input type="hidden" id="itemsPerPageHidden" name="size" value="${size}">
            <button type="submit" class="btn btn-primary">검색</button>
        </form>

    </div>




    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Select</th>
            <th>직원번호</th>
            <th>직원명</th>
            <th>직급</th>
            <th>전화번호</th>
            <th>이메일</th>
            <th>등록일</th>
            <th>수정일</th>
            <th>직원정보 메일발송</th>

        </tr>
        </thead>
        <tbody>
        <c:forEach var="employee" items="${EmployeeList}">
            <tr>
                <td><input type="checkbox" class="employee-select" data-id="${employee.id}"></td>
                <td>${employee.deptNo}</td>
                <td><a href="#" class="employee-detail" data-id="${employee.id}" data-toggle="modal" data-target="#detailModal">${employee.employeeName}</a></td>
                <td>${employee.position}</td>
                <td>${employee.phoneNm}</td>
                <td>${employee.email}</td>
                <td>${employee.createdAt}</td>
                <td>${employee.modifiedAt}</td>
                <td>
                    <button onclick="sendEmail(${employee.id})">메일 발송</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>


    <!-- 페이지 정보 -->
    <div class="d-flex justify-content-between mt-3">
        <div>
            <p>${currentPage} / ${totalPages} 페이지</p>
        </div>
        <div>
            <p>총 ${totalBoardCount}건</p>
        </div>
    </div>


    <!-- 페이징 -->
    <div class="d-flex justify-content-center">
        <ul class="pagination">
            <c:forEach var="pageNum" begin="1" end="${totalPages}">
                <li class="page-item ${currentPage == pageNum ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/list?page=${pageNum}&searchType=${searchType}&query=${query}&size=${size}&sortOrder=${sortOrder}&sortBy=${sortBy}">${pageNum}</a>
                </li>
            </c:forEach>
        </ul>
    </div>



    <!-- 등록 모달 -->
    <div class="modal" id="registerModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">직원 등록</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="registerForm" enctype="multipart/form-data" method="post" action="/save">
                        <div class="form-group">
                            <label for="deptNo">직원번호</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="deptNo" name="deptNo">
                                <div class="input-group-append">
                                    <button type="button" class="btn btn-info" onclick="checkDuplicate('registerForm')">중복 확인</button>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="employeeName">직원명</label>
                            <input type="text" class="form-control" id="employeeName" name="employeeName">
                        </div>
                        <div class="form-group">
                            <label for="position">직급</label>
                            <input type="text" class="form-control" id="position" name="position">
                        </div>
                        <div class="form-group">
                            <label for="phoneNm">전화번호</label>
                            <input type="text" class="form-control" id="phoneNm" name="phoneNm">
                        </div>
                        <div class="form-group">
                            <label for="email">이메일</label>
                            <input type="text" class="form-control" id="email" name="email">
                        </div>
                        <div class="form-group">
                            <label for="employFile">파일 추가</label>
                            <input type="file" class="form-control" id="employFile" name="employFile" multiple onchange="handleFileSelect(this, 'register')">
                        </div>
                        <div id="fileList" class="form-group"></div>
                        <button type="button" class="btn btn-primary" onclick="submitForm()">등록</button>
                    </form>
                </div>
            </div>
        </div>
    </div>



    <!-- Detail Modal -->
    <div class="modal" id="detailModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">직원 상세 정보</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <table class="table table-bordered">
                        <tr>
                            <th>직원번호</th>
                            <td id="detailDeptNo"></td>
                        </tr>
                        <tr>
                            <th>직원명</th>
                            <td id="detailEmployeeName"></td>
                        </tr>
                        <tr>
                            <th>직급</th>
                            <td id="detailPosition"></td>
                        </tr>
                        <tr>
                            <th>전화번호</th>
                            <td id="detailPhoneNm"></td>
                        </tr>
                        <tr>
                            <th>이메일</th>
                            <td id="detailEmail"></td>
                        </tr>
                        <tr>
                            <th>수정일</th>
                            <td id="detailModifiedAt"></td>
                        </tr>
                        <tr>
                            <th>첨부파일</th>
                            <td id="detailFiles"></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- 수정 모달 -->
    <div class="modal" id="updateModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">직원 수정</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="updateForm" enctype="multipart/form-data">
                        <input type="hidden" id="updateId" name="id">
                        <div class="form-group">
                            <label for="updateDeptNo">직원번호</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="updateDeptNo" name="deptNo">
                                <div class="input-group-append">
                                    <button type="button" class="btn btn-info" onclick="checkDuplicate('updateForm')">중복 확인</button>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="updateEmployeeName">직원명</label>
                            <input type="text" class="form-control" id="updateEmployeeName" name="employeeName">
                        </div>
                        <div class="form-group">
                            <label for="updatePosition">직급</label>
                            <input type="text" class="form-control" id="updatePosition" name="position">
                        </div>
                        <div class="form-group">
                            <label for="updatePhoneNm">전화번호</label>
                            <input type="text" class="form-control" id="updatePhoneNm" name="phoneNm">
                        </div>
                        <div class="form-group">
                            <label for="updateEmail">이메일</label>
                            <input type="text" class="form-control" id="updateEmail" name="email">
                        </div>

                        <div class="form-group">
                            <label for="updateEmployFile">파일 추가</label>
                            <input type="file" class="form-control" id="updateEmployFile" name="employFile" multiple onchange="handleFileSelect(this, 'update')">
                        </div>
                        <div id="updateFileList" class="form-group"></div>

                        <div class="form-group">
                            <label for="filesToDelete">기존 파일 삭제</label>
                            <div id="filesToDelete"></div>
                        </div>
                        <button type="button" class="btn btn-primary" onclick="updateFormSubmit()">수정</button>
                    </form>
                </div>
            </div>
        </div>
    </div>


</div>

<!-- Bootstrap and jQuery JS -->
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script>


    // 전역 배열을 초기화
    var selectedFiles = { register: [], update: [] };

    // 0. 데이터 오름 차순, 내림 차순 함수
    function sortData() {
        var sortOrder = $('#sortOrder').val();
        var sortBy = $('#sortBy').val();
        $('#sortOrderHidden').val(sortOrder);
        $('#sortByHidden').val(sortBy);
        $('#searchForm').submit();
    }


    // 0. 페이지 Size 함수 (5,10,15,20,50)
    function changeItemsPerPage() {
        var size = $('#itemsPerPage').val();
        var searchType = $('#searchType').val();
        var query = $('#query').val();
        var sortOrder = $('#sortOrderHidden').val();
        var sortBy = $('#sortByHidden').val();
        window.location.href = "${pageContext.request.contextPath}/list?page=1&searchType=" + searchType + "&query=" + query + "&size=" + size + "&sortOrder=" + sortOrder + "&sortBy=" + sortBy;
    }


    // 1. 직원 등록 버튼
    function submitForm() {

        var employeeName = $("#employeeName").val();
        var deptNo = $("#deptNo").val();
        var position = $("#position").val();
        var phoneNm = $("#phoneNm").val();
        var email = $("#email").val();

        var namePattern = /^[가-힣a-zA-Z]{1,10}$/;
        var deptNoPattern = /^\d{3}$/;
        var phonePattern = /^010-\d{4}-\d{4}$/;
        var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!namePattern.test(employeeName)) {
            alert("유효한 이름을 입력하세요. (한글 또는 영문, 1자 이상 10자 이하)");
            return;
        }
        if (!deptNo || deptNo.trim().length === 0) {
            alert("직원 번호는 공백 일 수 없습니다.");
            return;
        }
        if (!deptNoPattern.test(deptNo)) {
            alert("직원 번호는 3자리 숫자여야 합니다.");
            return;
        }
        if (!position || position.trim().length === 0) {
            alert("직급은 공백 일 수 없습니다.");
            return;
        }
        if (!phonePattern.test(phoneNm)) {
            alert("유효한 핸드폰 번호를 입력하세요. (예: 010-1234-5678)");
            return;
        }
        if (!emailPattern.test(email)) {
            alert("유효한 이메일을 입력하세요. (예: example@domain.com)");
            return;
        }

        var formData = new FormData($("#registerForm")[0]);

        // 전역 배열에 저장된 파일을 폼 데이터에 추가
        formData.delete('employFile'); // 기존 파일 삭제
        selectedFiles['register'].forEach(function(file) {
            formData.append('employFile', file);
        });

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/save",
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                alert("직원이 등록되었습니다.");
                location.reload();
            },
            error: function(error) {
                alert("이미 존재하는 직원번호 입니다.");
            }
        });
    }

    // 2. 직원 세부 조회
    $(document).ready(function(){
        $('.employee-detail').click(function(){
            var id = $(this).data('id');
            $.ajax({
                type: "GET",
                url: "${pageContext.request.contextPath}/detail/" + id,
                success: function(response) {
                    console.log("직원 세부 정보:", response); // 디버깅을 위해 응답 데이터를 콘솔에 출력
                    $('#detailDeptNo').text(response.deptNo);
                    $('#detailEmployeeName').text(response.employeeName);
                    $('#detailPosition').text(response.position);
                    $('#detailPhoneNm').text(response.phoneNm);
                    $('#detailEmail').text(response.email);
                    $('#detailModifiedAt').text(response.modifiedAt);

                    // 파일 목록 표시
                    var filesHtml = "";

                    if (response.files) {
                        response.files.forEach(function(file) {
                            filesHtml += '<a href="${pageContext.request.contextPath}/download?fileName=' + file.saveName + '&originalName=' + file.originalName + '">' + file.originalName + '</a><br>';
                            console.log(file.saveName);
                            console.log(file.originalName);
                        });
                    }
                    console.log("파일 다운로드 URL :" + filesHtml);
                    $('#detailFiles').html(filesHtml);
                },
                error: function(error) {
                    console.log("Error:", error);
                    alert("오류가 발생했습니다. 상세 정보를 불러올 수 없습니다.");
                }
            });
        });
    });


    // 3-1. 직원 수정 모달창
    function openUpdateModal() {
        var selectedEmployee = $('.employee-select:checked');
        if (selectedEmployee.length != 1) {
            alert("수정할 직원을 한 명 선택해주세요.");
            return;
        }

        var id = selectedEmployee.data('id');
        $.ajax({
            type: "GET",
            url: "/detail/" + id,
            success: function(response) {
                $('#updateId').val(response.id);
                $('#updateDeptNo').val(response.deptNo);
                $('#updateEmployeeName').val(response.employeeName);
                $('#updatePosition').val(response.position);
                $('#updatePhoneNm').val(response.phoneNm);
                $('#updateEmail').val(response.email);
                $('#updateModal').modal('show');

                var filesHtml = "";
                if (response.files) {
                    response.files.forEach(function(file) {
                        filesHtml += '<div><input type="checkbox" name="filesToDelete" value="' + file.id + '">' + file.originalName + '<br></div>';
                    });
                }
                $('#filesToDelete').html(filesHtml);
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }

    // 3-2. 직원 수정 버튼
    function updateFormSubmit() {

        var employeeName = $("#updateEmployeeName").val();
        var deptNo = $("#updateDeptNo").val();
        var position = $("#updatePosition").val();
        var phoneNm = $("#updatePhoneNm").val();
        var email = $("#updateEmail").val();

        var namePattern = /^[가-힣a-zA-Z]{1,10}$/;
        var deptNoPattern = /^\d{3}$/;
        var phonePattern = /^010-\d{4}-\d{4}$/;
        var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!namePattern.test(employeeName)) {
            alert("유효한 이름을 입력하세요. (한글 또는 영문, 1자 이상 10자 이하)");
            return;
        }
        if (!deptNo || deptNo.trim().length === 0) {
            alert("직원 번호는 공백 일 수 없습니다.");
            return;
        }
        if (!deptNoPattern.test(deptNo)) {
            alert("직원 번호는 3자리 숫자여야 합니다.");
            return;
        }
        if (!position || position.trim().length === 0) {
            alert("직급은 공백 일 수 없습니다.");
            return;
        }
        if (!phonePattern.test(phoneNm)) {
            alert("유효한 핸드폰 번호를 입력하세요. (예: 010-1234-5678)");
            return;
        }
        if (!emailPattern.test(email)) {
            alert("유효한 이메일을 입력하세요. (예: example@domain.com)");
            return;
        }

        var formData = new FormData($("#updateForm")[0]);

        // 전역 배열에 저장된 파일을 폼 데이터에 추가
        formData.delete('employFile'); // 기존 파일 삭제
        selectedFiles['update'].forEach(function(file) {
            formData.append('employFile', file);
        });


        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/update/" + $('#updateId').val(),
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                alert("직원이 수정되었습니다.");
                location.reload();
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }

    // 4. 직원 삭제
    function deleteEmployee() {
        var selectedEmployees = $('.employee-select:checked');
        if (selectedEmployees.length == 0) {
            alert("삭제할 직원을 선택해주세요.");
            return;
        }

        if (!confirm("삭제하시겠습니까?")) {
            return;
        }

        var ids = [];
        selectedEmployees.each(function() {
            ids.push($(this).data('id'));
        });

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/delete",
            traditional: true,
            data: { ids: ids },
            success: function(response) {
                alert("직원이 삭제되었습니다.");
                location.reload();
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }


    // 5. 직원 번호 중복 확인
    function checkDuplicate(formId) {

        var form = $('#' + formId);
        var deptNo = form.find('[name="deptNo"]').val();
        var deptNoPattern = /^\d{3}$/;

        if (deptNo.trim() === "") {
            alert("직원 번호를 입력하세요.");
            return;
        }

        if (!deptNoPattern.test(deptNo)) {
            alert("직원 번호는 3자리 숫자여야 합니다.");
            return;
        }

        $.ajax({
            type: "GET",
            url: "${pageContext.request.contextPath}/check-duplicate",
            data: { deptNo: deptNo },
            success: function(response) {
                if (response) {
                    alert("이미 존재하는 직원번호 입니다.");
                } else {
                    alert("사용 가능한 직원 번호입니다.");
                }
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }

    //6. 등록,수정 업로드 파일 List
    function handleFileSelect(input, type) {
        var files = input.files;
        var fileList = type === 'register' ? $('#fileList') : $('#updateFileList');

        // 선택된 파일을 전역 배열에 추가
        for (var i = 0; i < files.length; i++) {
            selectedFiles[type].push(files[i]);
        }

        // 파일 목록을 UI에 업데이트
        fileList.empty();
        for (var i = 0; i < selectedFiles[type].length; i++) {
            fileList.append('<div id="file-' + type + '-' + i + '">' + selectedFiles[type][i].name + ' <button type="button" class="btn btn-danger btn-sm" onclick="removeFile(' + i + ', \'' + type + '\')">X</button></div>');
        }
    }

    // 7. 등록,수정 업로드 파일 삭제
    function removeFile(index, type) {
        selectedFiles[type].splice(index, 1); // 선택된 파일 목록에서 제거

        // 파일 목록을 UI에 업데이트
        var fileList = type === 'register' ? $('#fileList') : $('#updateFileList');
        fileList.empty();
        for (var i = 0; i < selectedFiles[type].length; i++) {
            fileList.append('<div id="file-' + type + '-' + i + '">' + selectedFiles[type][i].name + ' <button type="button" class="btn btn-danger btn-sm" onclick="removeFile(' + i + ', \'' + type + '\')">X</button></div>');
        }
    }

    //8. 메일 발송
    function sendEmail(employeeId) {
        $.post("/send-email", {employeeId: employeeId}, function(response) {
            alert(response);
            var emailCount = response.split(": ")[1];
            $("#emailCount-" + employeeId).text(emailCount);
        });
    }

</script>
</body>
</html>


