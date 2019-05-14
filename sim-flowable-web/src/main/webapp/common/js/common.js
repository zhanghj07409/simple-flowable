
//浮点数加法运算
function FloatAdd(arg1,arg2,pos){

    var r1,r2,m;

    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}

    m=Math.pow(10,Math.max(r1,r2));

    return toNbits((arg1*m+arg2*m)/m,pos)

}

//浮点数减法运算
function FloatSub(arg1,arg2,pos){

    var r1,r2,m,n;

    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}

    m=Math.pow(10,Math.max(r1,r2));

//动态控制精度长度

    n=(r1>=r2)?r1:r2;

    return toNbits(((arg1*m-arg2*m)/m).toFixed(n),pos)

}



//浮点数乘法运算
function FloatMul(arg1,arg2,pos){

    var m=0,s1=arg1.toString(),s2=arg2.toString();

    try{m+=s1.split(".")[1].length}catch(e){}
    try{m+=s2.split(".")[1].length}catch(e){}

    return toNbits(Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m),pos)

}

//浮点数除法运算
function FloatDiv(arg1,arg2,pos){

    var t1=0,t2=0,r1,r2;

    try{t1=arg1.toString().split(".")[1].length}catch(e){}
    try{t2=arg2.toString().split(".")[1].length}catch(e){}

    with(Math){

        r1=Number(arg1.toString().replace(".",""));
        r2=Number(arg2.toString().replace(".",""));

        return toNbits((r1/r2)*pow(10,t2-t1),pos)

    }
}

//保留小数位数的方法控制
function toNbits(flt,pos) {

    var rd=1;

    for(var i=1;i<=parseInt(pos);i++)    {
        rd=rd*10;
    }

    if(parseFloat(flt) == flt){
        return Math.round(flt * rd) / rd;
    }
    else{
        return 0;
    }
}

/**
 * 分组查询
 */
var exeComQuery = function (baseParams) {
    var queryParams = $("#queryForm").serialize();
    queryParams = decodeURIComponent(queryParams);
    if (baseParams != null && baseParams != "") {
        queryParams = baseParams;
    }
    var qparams = toJson(queryParams);
    $('#group-grid').GroupGrid("setGridParam",qparams);
    closeSearch();
};

var exeComSearch = function (searchproperty) {
    var searchid = "#search_"+searchproperty;
    var searchpropertyvalue = $(searchid).val();
    var param = searchproperty+"="+searchpropertyvalue;
    var qparams = toJson(decodeURIComponent(param));
    $('#group-grid').GroupGrid("setGridParam",qparams);
};


/*onerror=handleErr;
var errorMessage="";

function handleErr(msg,url,l)
{
    errorMessage+="Error: " + msg + ";";
    errorMessage+="URL: " + url + ";";
    errorMessage+="Line: " + l + ";";
    Ajax.req({
        url: '../../common/addjserrlog.do',
        type: "json",
        method: "POST",
        params: {
            errorMessage : errorMessage
        },
        ok: function (text) {
            console.log(text.result);
        },
        err: function (text) {
            console.log("fail");
        }
    });
    return true;
}*/

