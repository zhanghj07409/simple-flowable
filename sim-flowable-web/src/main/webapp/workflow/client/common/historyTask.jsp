<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<meta http-equiv="X-UA-Compatible" content="IE=9;IE=8;IE=7;IE=EDGE">
<head>
    <title></title>
    <link rel="stylesheet" href="../../../common/css/bootstrap.css">
    <link type="text/css" rel="stylesheet" href="../../../common/css/font-awesome.css"/>
    <link type="text/css" rel="stylesheet" href="../../../common/css/ui.jqgrid.css"/>
    <link type="text/css" rel="stylesheet" href="../../../common/css/ace.css"/>

    <script type="text/javascript" src="../../../common/js/jquery-2.2.0.min.js"></script>
    <script type="text/javascript" src="../../../common/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../../../common/js/jqGrid/jquery.jqGrid.src.js"></script>
    <script type="text/javascript" src="../../../common/js/jqGrid/i18n/grid.locale-cn.js"></script>
</head>
<body>
<%
    String processInstanceId = request.getParameter("processInstanceId");
    String processDefinitionId = request.getParameter("processDefinitionId");
    if(processDefinitionId==null||processDefinitionId==""){
        processDefinitionId = request.getParameter("processDefinitionKey");
    }
    String url = "/gateway/modeler/flowable-modeler/workflow/query/hisTask.do?processInstanceId=" + processInstanceId+"&processDefinitionId="+processDefinitionId;
%>

<div style="margin-left: 60px;">
    <p></p>
    <br>
    <div class="page-content" id="task-r">
        <div class="row">
            <div class="col-xs-12">
                <table id="task"></table>
                <div id="task-grid-pager"></div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">


    $(function(){
        pageInit();
    });
    function pageInit(){
        jQuery("#task").jqGrid(
            {
                url : "<%=url%>",
                datatype : "json",
                colNames : [ 'id', 'activityName', 'assigneeName', 'startTime', 'endTime'],
                colModel : [
                    {name : 'id',index : 'id',width : 120},
                    {name : 'activityName',index : 'activityName',width : 120},
                    {name : 'assigneeName',index : 'assigneeName',width : 120},
                    {name : 'startTime',index : 'startTime',width : 120,align : "right"},
                    {name : 'endTime',index : 'tax',width : 120,align : "right"}
                ],
                height:600,
                rowNum : 10,
                rowList : [ 10, 20, 30 ],
                pager : '#task-grid-pager',
                sortname : 'id',
                mtype : "post",
                viewrecords : true,
                sortorder : "desc",
                caption : ""
            });
        jQuery("#task").jqGrid('navGrid', '#task-grid-pager', {edit : false,add : false,del : false});
    }



</script>
</body>
</html>

