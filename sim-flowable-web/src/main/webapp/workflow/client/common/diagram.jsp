<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/workflow/manage/display/jquery.qtip.min.css" />
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/workflow/manage/display/displaymodel.css" />
    <script type="text/javascript" src="../../../common/js/jquery-2.2.0.min.js"></script>
    <link rel="stylesheet" href="../../../common/css/bootstrap.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/client/common/flowable-cfg.js"></script>
</head>
<body>
    <%
        String processInstanceId = request.getParameter("processInstanceId");
        String type = request.getParameter("type");
    %>
    <div style="overflow-x: auto;width: 1200px;height: 480px">
        <div id="bpmnModel" ></div>
    </div>
    <script type="text/javascript">

        var processInstanceId = "<%=processInstanceId%>";
        var type = "<%=type%>";

        $.ajax({
            type: "GET",
            url: FLOWABLE.CONFIG.contextRoot + "/app-api/rest/process-instances/" + processInstanceId,
            data: {},
            async: false,
            dataType: "json",
            success: function (data) {
                if(data.ended){
                    $("#bpmnModel").attr('data-history-id', data.id);
                }
            }
        });

        $("#bpmnModel").attr("data-model-id",processInstanceId);
        $("#bpmnModel").attr("data-model-type",type);
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/jquery.qtip.min.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/raphael.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/bpmn-draw.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/bpmn-icons.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/Polyline.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/workflow/manage/display/displaymodel.js"></script>
    </body>
</html>
