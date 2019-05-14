/**
 * 扩展JQUERY，提供命名空间支持.
 */
if (typeof jQuery != "undefined"
    && (typeof jQuery.namespace == "undefined" || typeof jQuery.ns == "undefined")) {
    var ddNamespace = function() {
        var a = arguments, o = null, i, j, d;
        for (i = 0; i < a.length; i = i + 1) {
            d = a[i].split(".");
            o = window;
            for (j = 0; j < d.length; j = j + 1) {
                o[d[j]] = o[d[j]] || {};
                o = o[d[j]];
            }
        }
        return o;
    };

    var ddNsPropExists = function(o, key) {
        var ns = dflydevNamespace(o);
        return typeof ns[key] != "undefined";
    };
    jQuery.namespace = ddNamespace;
    jQuery.ns = jQuery.namespace;
    jQuery.nsPropExists = ddNsPropExists;
}

/**
 * 封装AJAX.
 */
$.ns('Ajax');
Ajax.request = function(con) {
    var dataType = con.dataType == null ? 'json' : con.dataType;
    var headerType = con.headers == null ? false : true;
    $.ajax({
        cache:false,
        type: con.method == null ? 'POST' : con.method,
        dataType: dataType,
        url: con.url,
        data: con.params,
        async: con.async == null ? true : con.async,
        beforeSend: function(xhr) {
            if(headerType) {
                xhr.setRequestHeader('REQUEST-TYPE', 'AJAX');
                xhr.setRequestHeader('Accept', 'application/json');
                xhr.setRequestHeader('Content-Type', 'application/json');
            }
            //$('#maskdiv').doMask('正在处理,请稍候...');
        },
        success: function(result) {
            if (!result) {
                con.err.call(this, '返回数据为空!');
            }
            else if (result.success == true && con.ok != null && $.isFunction(con.ok)) {
                con.ok.call(this, result);
            }
            else if (result.success == false && $.isFunction(con.err)) {
                con.err.call(this, result);
            }
            else{
                con.ok.call(this, result);
            }
        },
        error: function(result) {
            if (con.err != null && $.isFunction(con.err)) {
                con.err.call(this, result);
            }
        },
        complete: function(xhr) {
            //$('#maskdiv').doUnMask();
        }
    });
};
Ajax.req = Ajax.request;
