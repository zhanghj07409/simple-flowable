function reload(url) {

    var maxTime = 60*10; // 锁屏时间
    var time = maxTime;

    function screenAllCheck(){
        var childIframeObj = $('body').find('iframe');
        while(childIframeObj.length){
            childIframeObj.contents().on('mousemove mousedown keydown', 'body' , screenCheck);
            childIframeObj = childIframeObj.contents().find('iframe');
        }
    }
    screenPolice = setInterval(screenAllCheck, 1000);

    var screenCheck = function(e) {
        time = maxTime; // reset
    };
    var intervalId = setInterval(function () {
        time--;
        // console.log(time);
        if (time <= 0) {
            ShowInvalidLoginMessage(url);
            clearInterval(intervalId);
        }
    }, 1000);

    function ShowInvalidLoginMessage(url) {
        alert("您已经长时间没操作了，即将退出系统");
        //exp:关闭页面
        top.location.href = url;
    }

}

