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
                            请假流程
                        </header>

                        <fieldset>
                            <br/>
                            <section>
                                <label class="input">
                                    <input id="id" name="id" placeholder="  Id"/></label>
                            </section>


                            <br/>
                            <section>
                                <label class="input"><input id="name" name="name" placeholder="  原因"/></label></label>
                            </section>

                            <br/>

                        </fieldset>

                        <footer>
                            <button id="login_btn" class="btn btn-primary">送审</button>
                        </footer>

                    </form>
                </div>
            </div>

<script>

    $(function() {

        $("#login_btn").click(function userformSubmit() {
            var id = $("#id").val();
            var name = $("#name").val();
            $.ajax({
                url: "/gateway/simbasic/biz/send",
                type: 'post',
                data: {"id":id,"name":name},
                success: function (data) {
                    alert("操作成功！");
                    window.parent.location.href = "/index.html";
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