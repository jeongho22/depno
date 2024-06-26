<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

        <!-- 검색 폼 -->
        <form method="get" action="${pageContext.request.contextPath}/list" class="form-inline ml-auto">
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
            <th>수정일</th>
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
                <td>${employee.modifiedAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>


    <div class="d-flex justify-content-center">
        <ul class="pagination">
            <c:forEach var="pageNum" begin="1" end="${totalPages}">
                <li class="page-item">
                    <a class="page-link" href="${pageContext.request.contextPath}/list?searchType=${searchType}&query=${query}&page=${pageNum}">${pageNum}</a>
                </li>
            </c:forEach>
        </ul>
    </div>

    <!-- Create Modal -->
    <div class="modal" id="registerModal">
        <div class="modal-dialog">
            <div class="modal-content">

                <div class="modal-header">
                    <h4 class="modal-title">직원 등록</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>

                <!-- Modal body -->
                <div class="modal-body">
                    <form id="registerForm">

                        <div class="form-group">
                            <label for="deptNo">직원번호:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="deptNo" name="deptNo">
                                <div class="input-group-append">
                                    <button type="button" class="btn btn-info" onclick="checkDuplicate('registerForm')">중복 확인</button>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="employeeName">직원명:</label>
                            <input type="text" class="form-control" id="employeeName" name="employeeName">
                        </div>
                        <div class="form-group">
                            <label for="position">직급:</label>
                            <input type="text" class="form-control" id="position" name="position">
                        </div>
                        <div class="form-group">
                            <label for="phoneNm">전화번호:</label>
                            <input type="text" class="form-control" id="phoneNm" name="phoneNm">
                        </div>
                        <div class="form-group">
                            <label for="email">이메일:</label>
                            <input type="text" class="form-control" id="email" name="email">
                        </div>
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
                    </table>
                </div>
            </div>
        </div>
    </div>


    <!-- Update Modal -->
    <div class="modal" id="updateModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">직원 수정</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="updateForm">
                        <input type="hidden" id="updateId" name="id">


                        <div class="form-group">
                            <label for="updateDeptNo">직원번호:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="updateDeptNo" name="deptNo">
                                <div class="input-group-append">
                                    <button type="button" class="btn btn-info" onclick="checkDuplicate('updateForm')">중복 확인</button>
                                </div>
                            </div>
                        </div>



                        <div class="form-group">
                            <label for="updateEmployeeName">직원명:</label>
                            <input type="text" class="form-control" id="updateEmployeeName" name="employeeName">
                        </div>
                        <div class="form-group">
                            <label for="updatePosition">직급:</label>
                            <input type="text" class="form-control" id="updatePosition" name="position">
                        </div>
                        <div class="form-group">
                            <label for="updatePhoneNm">전화번호:</label>
                            <input type="text" class="form-control" id="updatePhoneNm" name="phoneNm">
                        </div>
                        <div class="form-group">
                            <label for="updateEmail">이메일:</label>
                            <input type="text" class="form-control" id="updateEmail" name="email">
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

    // 1. 직원 등록
    function submitForm() {
        var formData = $("#registerForm").serialize();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/save",
            data: formData,
            success: function(response) {
                alert("직원이 등록되었습니다.");
                location.reload();
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
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
                    $('#detailDeptNo').text(response.deptNo);
                    $('#detailEmployeeName').text(response.employeeName);
                    $('#detailPosition').text(response.position);
                    $('#detailPhoneNm').text(response.phoneNm);
                    $('#detailEmail').text(response.email);
                    $('#detailModifiedAt').text(response.modifiedAt);
                },
                error: function(error) {
                    alert("오류가 발생했습니다. 상세 정보를 불러올 수 없습니다.");
                }
            });
        });
    });


    // 3-1. 직원 수정
    function openUpdateModal() {
        var selectedEmployee = $('.employee-select:checked');
        if (selectedEmployee.length != 1) {
            alert("수정할 직원을 한 명 선택해주세요.");
            return;
        }

        var id = selectedEmployee.data('id');
        $.ajax({
            type: "GET",
            url: "${pageContext.request.contextPath}/detail/" + id,
            success: function(response) {
                $('#updateId').val(response.id);
                $('#updateDeptNo').val(response.deptNo);
                $('#updateEmployeeName').val(response.employeeName);
                $('#updatePosition').val(response.position);
                $('#updatePhoneNm').val(response.phoneNm);
                $('#updateEmail').val(response.email);
                $('#updateModal').modal('show');
            },
            error: function(error) {
                alert("오류가 발생했습니다. 상세 정보를 불러올 수 없습니다.");
            }
        });
    }

    // 3-2. 직원 수정
    function updateFormSubmit() {
        var formData = $("#updateForm").serialize();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/update/" + $('#updateId').val(),
            data: formData,
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
        if (deptNo.trim() === "") {
            alert("직원 번호를 입력하세요.");
            return;
        }
        $.ajax({
            type: "GET",
            url: "${pageContext.request.contextPath}/check-duplicate",
            data: { deptNo: deptNo },
            success: function(response) {
                if (response) {
                    alert("직원 번호가 중복됩니다.");
                } else {
                    alert("사용 가능한 직원 번호입니다.");
                }
            },
            error: function(error) {
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }


</script>

</body>
</html>
