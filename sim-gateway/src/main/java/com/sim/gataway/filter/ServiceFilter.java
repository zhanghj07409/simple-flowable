package com.sim.gataway.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class ServiceFilter implements Filter {

    private String skipPattern="";
    private boolean enable=false;
    private Pattern pattern=null;

    public void destroy() {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest $_request = (HttpServletRequest) request;
        HttpServletResponse $_response = (HttpServletResponse) response;
        String servletPath = $_request.getServletPath();
        HttpSession session = $_request.getSession();
        session.getId();
        Matcher matcher = pattern.matcher(servletPath.toLowerCase());
        if(!matcher.matches() && enable ){

        }
        chain.doFilter($_request, $_response);
    }

    public void init(FilterConfig config) throws ServletException {
        skipPattern=config.getInitParameter("skipPattern");
        String $_enable=config.getInitParameter("enable");
        $_enable=(null==$_enable)?"":$_enable;
        enable="true".equals($_enable.trim().toLowerCase());
        pattern=Pattern.compile(".*?("+skipPattern+")+.*");
    }

}
