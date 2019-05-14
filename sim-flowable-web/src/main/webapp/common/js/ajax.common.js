function Commons(){
    //来处理所有的http请求
    this.getData = function(obj_array_url) {
        var data = [];
        for (var i = obj_array_url.length - 1; i >= 0; i--) {
            $.ajax({
                url:obj_array_url[i],
                method:"get",
                async:false,
                dataType:"json",
                success:function(result, status, xhr) {
                    data.push(result);
                },
                error:function(XMLHttpRequest, status, jqXHR, textStatus, e) {
                    console.error("getAllDataCS  CS数据状态文本 " + status);
                }
            });
        }
        return data;
    };
    this.getText = function(obj_array_url){
        var data = [];
        for (var i = obj_array_url.length - 1; i >= 0; i--) {
            $.ajax({
                url:obj_array_url[i],
                method:"get",
                async:false,
                dataType:"text",
                success:function(result, status, xhr) {
                    data.push(result);
                },
                error:function(XMLHttpRequest, status, jqXHR, textStatus, e) {
                    console.error("getAllDataCS  CS数据状态文本 " + status);
                }
            });
        }
        return data;
    };

}