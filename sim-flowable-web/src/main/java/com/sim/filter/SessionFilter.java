package com.sim.filter;

import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class SessionFilter implements Filter {
    private String filterPattern;
    private RedisTemplate redisTemplate;
    private static List<String> roleList=new ArrayList<String>();


    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession();

        // 继续向下执行
        chain.doFilter(req, resp);

    }

    public void init(FilterConfig config) throws ServletException {
        filterPattern = config.getInitParameter("filterPattern");
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

}
