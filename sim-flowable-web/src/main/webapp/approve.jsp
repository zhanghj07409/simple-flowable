<%@page language="java" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="zh">
<head>

    <meta charset="utf-8"/>
    <title></title>

    <link rel="stylesheet" type="text/css" media="screen" href="../../../common/css/bootstrap.css"/>
    <script type="text/javascript" src="../../../common/js/jquery-2.2.0.min.js"></script>
<%
String id= request.getParameter("id");
    String name= request.getParameter("name");

%>
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
                                    <input id="id" name="id" value="<%=id%>" readonly="true"/></label>
                            </section>


                            <br/>
                            <section>
                                <label class="input"><input id="name" name="name" value="<%=name%>" readonly="true"/></label></label>
                            </section>

                            <br/>

                        </fieldset>

                    </form>
                </div>
            </div>

<script>

    $(function() {




    });

</script>


</body>
</html>