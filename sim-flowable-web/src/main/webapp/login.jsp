<%@page language="java" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="zh">
<head>

    <meta charset="utf-8"/>
    <title></title>

    <link rel="stylesheet" type="text/css" media="screen" href="common/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="common/css/smartadmin-production.min.css"/>
    <script type="text/javascript" src="common/js/jquery-2.2.0.min.js"></script>
    <script type="text/javascript" src="common/js/base64.js"></script>
    <script type="text/javascript" src="common/js/adapter.js"></script>

</head>

<body class="animated fadeInDown  desktop-detected pace-done">

<header id="header">

    <div id="logo-group">

        <span id="logo"><a href=""><img src="" alt=""/></a>  </span>

    </div>

</header>

            <div style="width:450px;margin: 40px auto;">
                <div class="well no-padding">
                    <form id="login" name="login" onSubmit="return false;" class="smart-form client-form" >
                        <header>
                            用户登录
                        </header>

                        <fieldset>
                            <br/>
                            <section>
                                <label class="input">
                                    <input id="name" name="name" placeholder="  用户名"/></label>
                            </section>


                            <br/>
                            <section>
                                <label class="input"><input type="password" id="password" name="password" placeholder="  密码"/></label>
                            </section>

                            <br/>

                        </fieldset>

                        <footer>
                            <button id="login_btn" class="btn btn-primary">登录</button>
                        </footer>

                    </form>
                </div>
            </div>

<script>

    $(function() {

        $("#login_btn").click(function userformSubmit() {
            var name = $("#name").val();
            var password = $("#password").val();
            if (name == '') {
                alert('用户名不能为空!');
                return;
            } else if (password == '') {
                alert('密码不能为空!');
                return;
            }
            $.ajax({
                url: "/gateway/simbasic/login/in",
                type: 'post',
                data: {"name":name,"password":Base64.encode(password)},
                success: function (data) {
                    window.location.href = "/index.html";
                },
                error:function (data) {
                    alert(data);
                }
            });
        });


    });

</script>


</body>
</html>