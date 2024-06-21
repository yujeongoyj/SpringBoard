<%@page language="java" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>회원 가입</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
<div class="container-fluid h-100">
    <form action="/user/register" method="POST">
        <div class="row justify-content-center">
            <div class="col-4">
                <label for="username">아이디</label>
                <input type="text" name="username" id="username" class="form-control" oninput="disableButton()">
                <a class="btn btn-outline-primary" onclick="validateUsername()">중복확인</a>
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-4">
                <label for="password">비밀번호</label>
                <input type="password" name="password" id="password" class="form-control">
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-4">
                <label for="nickname">닉네임</label>
                <input type="text" name="nickname" id="nickname" class="form-control">
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-4 text-center">
                <input id="btnSubmit" type="submit" class="btn btn-outline-primary" value="회원 가입" disabled>
            </div>
        </div>
    </form>
<script>
    // jQuery의 경우, HTML DOM 객체를 선택할 때 사용하던
    // document.selectElemnet... 들을 간단하게 $() 로 사용하기 된다
/*    let username = document.getElementById('username');
    console.log(username);
    let username = $('#username');
     jQuery는 $() 로 선택된 객체의 속성값들을 수정할 수 있게 도와주는 함수가 다수 준비되어 있다.
    $('#username').val('jQuery 로 입력함')
    $('#username').css('color', 'red');

    $('#username').attr('disabled', 'true')

    */

    // AJAX 비동기화 통신
    // 단, AJAX를 스프링에서 사용하기 위해서는
    // 별도의 컨트롤러가 필요하다
    // 해당 컨트롤러에서는 페이지 이동을 위한 컨트롤러가 아니라
    // 어떠한 값 또는 객체를 리턴해주는 것이 목표인 컨트롤러다.
    // 그러한 컨트롤러는 우리가  RestContoller 라고 하고
    // 특정 URL을 접속했을 때 어떤 값을 리턴해주는 방식의 서비스를
    // Restful Service라고도 한다.
    function validateUsername() {
        let username = $('#username').val();
        $.ajax({
            url: '/user/validateUsername',
            type: 'get',
            data: {
                'username': username
            },
            success: (result) => {
                if(result.result === 'success'){
                    Swal.fire({
                        'title': '가입 가능한 아이디입니다.'
                    }).then(() => {
                        $('#btnSubmit').removeAttr('disabled');
                    })
                } else {
                    Swal.fire({
                        'title': '중복된 아이디입니다.',
                        'icon': "warning"
                    })
                }
            }

        });
    }

    function disableButton() {
        $('#btnSubmit').attr('disabled', 'true');
    }
</script>
</div>
</body>