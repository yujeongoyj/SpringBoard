<%@page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>${boardDTO.id}번 게시글</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</head>
<body>
<div class="container-fluid">
    <div class="row justify-content-center">
        <div class="col-6">
            <table class="table table-striped">
                <tr>
                    <th>글 번호</th>
                    <td>${boardDTO.id}</td>
                </tr>
                <tr>
                    <th>제목</th>
                    <td>${boardDTO.title}</td>
                </tr>
                <tr>
                    <th>작성자</th>
                    <td>${boardDTO.nickname}</td>
                </tr>
                <tr>
                    <th>작성일</th>
                    <td><fmt:formatDate value="${boardDTO.entryDate}" pattern="yyyy년 MM월 dd일 E요일 HH시 mm분 ss초"/></td>
                </tr>
                <tr>
                    <th>수정일</th>
                    <td><fmt:formatDate value="${boardDTO.modifyDate}" pattern="yyyy년 MM월 dd일 E요일 HH시 mm분 ss초"/></td>
                </tr>
                <tr>
                    <th colspan="2" class="text-center">내용</th>
                </tr>
                <tr>
                    <td colspan="2" class="text-center">${boardDTO.content}</td>
                </tr>
                <c:if test="${boardDTO.writerId eq logIn.id}">
                    <tr class="text-center">
                        <td class="text-center" colspan="3">
                            <a class="btn btn-outline-success" href="/board/update/${boardDTO.id}">수정하기</a>
                            <a class="btn btn-outline-danger" href="/board/delete/${boardDTO.id}">삭제하기</a>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <td colspan="3" class="text-center">
                        <a class="btn btn-outline-secondary" href="/board/showAll">목록으로</a>
                    </td>
                </tr>
            </table>
            <table class="table table-primary table-striped">
                <tr class=" text-center">
                    <td>댓글</td>

                    <form action="/reply/insert/${boardDTO.id}" method="post">
                        <td colspan="5">
                            <input type="text" name="content" class="form-control" placeholder="댓글">
                        </td>
                        <input type="submit" class="btn btn-outline-success" value="작성하기">
                    </form>
                    </td>
                </tr>
                <c:forEach items="${replyList}" var="reply">
                    <tr>
                        <td>${reply.id}</td>
                        <td>${reply.nickname}</td>
                        <c:choose>
                            <c:when test="${reply.writerId eq logIn.id}">

                                <form action="/reply/update/${reply.id}" method="post">
                                    <td>
                                        <input type="text" class="form-control" name="content"
                                               value="${reply.content}">
                                    </td>
                                    <td>
                                    <span>
                                        AT <fmt:formatDate value="${reply.modifyDate}" pattern="y년M월d일"/>
                                    </span>
                                    </td>
                                    <td>
                                        <input type="submit" class="btn btn-outline-primary" value="수정">
                                    </td>
                                    <td>
                                        <a href="/reply/delete/${reply.id}" class="btn btn-outline-warning">삭제</a>
                                    </td>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <td>
                                    <input type="text" class="form-control" name="content"
                                           value="${reply.content}" disabled>
                                </td>
                                <td>
                                    <span>
                                        AT <fmt:formatDate value="${reply.modifyDate}" pattern="y년M월d일"/>
                                    </span>
                                </td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>

            </table>

        </div>
    </div>
</div>
</body>
</html>