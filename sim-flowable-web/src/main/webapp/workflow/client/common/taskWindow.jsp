<%@ page import="java.util.Enumeration" %>
<%@ page import="java.net.URLDecoder" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<meta http-equiv="X-UA-Compatible" content="IE=9;IE=8;IE=7;IE=EDGE">
<head>
    <title>策赢平台</title>
</head>
<body>
<style>
    #myTab{
        border-top: 1px #ddd solid;
        border-bottom: 0;
    }
    #myTab>li>a{
        width: 80px;
        padding: 7px 0 8px;
        text-align: center;
        font-size: 14px;
        border-color: #ddd;
        color: #969696;
    }
    #myTab > li.active > a, .nav-tabs > li.active > a:hover, #myTab > li.active > a:focus{
        border-top: 2px #cb766d solid;
        color: #cb766d;
    }
    #myTab > li.active{
        border-top: 2px #cb766d solid;
    }
    #myTab > li{
        border-top: 2px #ddd solid;
    }
    .nav-tabs>li>a:hover{
        color: #cb766d;
    }
    #myTabContent {
        border: 0;
    }
    .textarea-wrap {
        width: 400px;
        height: 240px;
        display: none;
        transition: all .6s;
        text-align: center;
        opacity: 1;
        border: 1px solid black;
        position: fixed;
        top: 16%;
        right: 66%;
        z-index: 223;
        background-color: #999;
    }
    .btns{
        border: 0;
        background-color: #b81c22;
        font-size: 14px;
        font-family: "Microsoft YaHei";
        font-weight: 400;
        margin: 10px;
        padding: 6px 12px;
        color: white;
        text-align: center;
    }
    table.table-striped > tbody > tr:nth-child(2n+1) {
        background-color: #f5f6fa;
    }
    table{
        font-size: 12px;
        font-family: "Microsoft YaHei";
        color: #323232;
    }
</style>



<script type="text/javascript" src="${pageContext.request.contextPath}/workflow/client/common/flowable-cfg.js"></script>

<link rel="stylesheet" href="../../../common/css/bootstrap.css">
<link type="text/css" rel="stylesheet" href="../../../common/css/ace.css"/>

<script type="text/javascript" src="../../../common/js/jquery-2.2.0.min.js"></script>
<script type="text/javascript" src="../../../common/js/adapter.js"></script>
<script type="text/javascript" src="../../../common/js/bootstrap.min.js"></script>

<script type="text/javascript">
    var urlParam = {type: "runtime"};
</script>
<%
    //放弃优先权的按钮条件表达式
    String abandon = "${condition2=='disagree'}";
    String sentback = "${condition3=='return'}";
    Enumeration<String> requestMaps = request.getParameterNames();
    while (requestMaps.hasMoreElements()){
        String elementName = requestMaps.nextElement();
        String elementValue = request.getParameter(elementName);
        if("name".indexOf(elementName) > -1){
            elementValue = URLDecoder.decode(elementValue,"utf-8");
        }
%>
<script type="text/javascript">
    urlParam["<%=elementName%>"] = "<%=elementValue%>";
</script>
<%
    }
%>



<ul id="myTab" class="nav nav-tabs">
    <li class="active">
        <a href="#home" data-toggle="tab">基本信息</a>
    </li>
    <li><a href="#diagram" data-toggle="tab">流程图</a></li>
    <li><a href="#hisInfo" data-toggle="tab">历史信息</a></li>
</ul>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" id="home"></div>
    <div class="tab-pane fade" id="diagram"></div>
    <div class="tab-pane fade" id="hisInfo"></div>
</div>




<script type="text/javascript">

    var firstPageUrl = "";
    var processInstanceVariables;
    var businesskey ;
    $(function () {
        processInstanceVariables = queryVariables(urlParam["taskId"]);
        businesskey = queryBusinessKey(urlParam["taskId"]);
        $.extend(urlParam,processInstanceVariables);
        if(urlParam["formKey"] != undefined && urlParam["formKey"] != ""){
            firstPageUrl = "${pageContext.request.contextPath}" + urlParam["formKey"];
        }
        if(businesskey!= undefined && businesskey != ""){
            firstPageUrl = firstPageUrl+"?urid="+businesskey;
        }
        $("#home").addClass('in').addClass("active");
        if(urlParam["formKey"] != undefined && urlParam["formKey"] != "") {
            loadHtml("home", firstPageUrl, urlParam, function () {
                addProcessButton();
                $('#myTab a:first').tab('show');
            });
        } else {
            addProcessButton();
        }
        $("#diagram").height(document.documentElement.clientHeight - 200);
    });

    $("#myTab a").click(function (e) {
        e.preventDefault();
        $(this).tab('show');
        var href = $(this).attr("href");
        var $targetPane = $(href);
        if($targetPane.children().length > 0){
            // need not dealing with it
        }else{
            if(href == "#home"){
                $targetPane.addClass('in').addClass("active");
                loadHtml("home",firstPageUrl,urlParam,function () {
                    addProcessButton();
                });
            }
            if(href == "#diagram"){
                $targetPane.addClass('in').addClass("active");
                var url = "${pageContext.request.contextPath}/workflow/client/common/diagram.jsp";
                loadHtml("diagram",url,urlParam);
            }
            if(href == "#hisInfo"){
                $targetPane.addClass('in').addClass("active");
                var url = "${pageContext.request.contextPath}/workflow/client/common/historyTask.jsp";
                loadHtml("hisInfo",url,urlParam);
            }
        }
    });


    //使用get方法加载页面
    function loadHtml(tabPaneId,url,param,successFunc){
        $.get(url,param,function (data,status,xhr) {
            $("#" + tabPaneId).html(data);
            if(successFunc){
                successFunc(data,status,xhr);
            }
        });
    }

    //基本信息,添加流程图中按钮
    function addProcessButton(){
        $.ajax({
            url: FLOWABLE.CONFIG.contextRoot + "/workflow/query/transitions.do",
            data: urlParam,
            success: function(data){
                var buttonType = data.buttonType;
                if(buttonType == "normal"){
                    addAgreeButton("home",urlParam["taskId"],data.buttonValue);
                }else {
                    addClaimButton("home",urlParam["taskId"]);
                }
            },
            dataType: "json"
        });
    }

    /**
     * 添加同意按钮
     * */
    function addAgreeButton(tabId,taskId,buttonData) {
        var html = '<div style="height: 40px;margin:5px 0px 5px 10px;"><div style="float: left;">';
        if(buttonData && buttonData instanceof Array) {
            for (var i = 0; i < buttonData.length; i++) {
                var condition = buttonData[i].conditionExpression;
                if(check(buttonData[i].name)){
                    html += '<button class="btns" style="margin-right: 10px;" onclick=openTaskWindow("' + taskId + '","' + condition + '","'+buttonData[i].name+'")>' + buttonData[i].name + '</button>';
                } else {
                    html += '<button class="btns" style="margin-right: 10px;" onclick=completeTask("' + taskId + '","' + condition + '","'+buttonData[i].name+'")>' + buttonData[i].name + '</button>';
                }
            }
            $("#" + tabId).prepend(html);
        }
    }
    function check(buttonName){
        var arr = ["不同意","否决","取消","拒绝","拒签"];
        for(var i=0;i < arr.length;i++){
            if(arr[i] == buttonName){
                return true;
            }
        }
        return false;
    }

    function openTaskWindow(taskId,conditionExpression,buttonName) {
        conditionExpression = conditionExpression.replace("$","*1*").replace("{","*2*").replace("==","*3*").replace("}","*4*");
        if (buttonName == "否决"){
            buttonName = "disagree";
        }else if(buttonName == "拒签"){
            buttonName = "reject";
        }else {
            buttonName = "";
        }
        var url = "${pageContext.request.contextPath}/workflow/client/common/commentsAdd.jsp?taskId=" + taskId + "&conditionExpression=" + conditionExpression+"&buttonName="+buttonName;

    }

    //添加接收任务按钮
    function addClaimButton(tabId,taskId) {
        var html = '<div style="height: 40px;margin:5px 0px 5px 10px;"><div style="float: left;">'
            + '<button class="btns" style="margin-right: 10px;" onclick=claimTask("' + taskId + '")>接收</button>'
            + '</div></div>';
        $("#" + tabId).prepend(html);
    }

    var backtask ;
    var backcondition;
    //完成任务
    function completeTask(taskId,conditionExpression,buttonName) {
        if ("<%=sentback%>"==conditionExpression){
            $(".textarea-wrap").show();
            backtask = taskId;
            backcondition = conditionExpression;
            return;
        }
        var params = {taskId: taskId, conditionExpression: conditionExpression,description:buttonName};

        if ("<%=abandon%>"==conditionExpression){
            $.extend(params);
        }else {
            var variables = getLoadHtmlVariables();
            if (!variables){
                return;
            }
            $.extend(params,variables);
        }

        $('button').attr("disabled",true);
        Ajax.req({
            url: FLOWABLE.CONFIG.contextRoot + "/workflow/save/complete/task.do",
            headers : true,
            params: JSON.stringify(params),
            ok: function (result) {
                alert("消息"+result.msg);
                $('button').removeAttr("disabled");
            },
            err: function(result){
                alert("消息"+result.msg);
                $('button').removeAttr("disabled");
            }
        });
    }
    
    //接收任务
    function claimTask(taskId) {
        var params = {taskId : taskId};
        Ajax.req({
            url: FLOWABLE.CONFIG.contextRoot + '/workflow/save/claim/task.do',
            method: "POST",
            params: params,
            dataType: "json",
            ok: function (result) {
                if(parent.closeWinFn){
                    parent.closeWinFn();
                }
                if(closeWinFn){
                    closeWinFn();
                }
            }
        });
    }
    //获取开启流程时的参数
    function queryVariables(taskId){
        var retData;
        $.ajax({
            url: FLOWABLE.CONFIG.contextRoot + "/workflow/rest/query/variables.do",
            data: {taskId : taskId},
            success: function(data){
                retData = data;
            },
            dataType: "json",
            async: false
        });
        return retData;
    }
    //获取业务主键
    function queryBusinessKey(taskId){
        var retData;
        $.ajax({
            url: FLOWABLE.CONFIG.contextRoot + "/workflow/rest/query/getBusinessKey.do",
            data: {taskId : taskId},
            success: function(text){
                retData = text["businessKey"];
            },
            dataType: "json",
            async : false
        });
        return retData;
    }

    function getLoadHtmlVariables() {
        try {
            if (getVariables && typeof(getVariables) == "function" ) {
                return getVariables();
            }
        }catch(error){
            return {};
        }
        return {};
    }

</script>
</body>
</html>
