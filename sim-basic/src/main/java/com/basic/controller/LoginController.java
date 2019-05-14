package com.basic.controller;

import com.basic.service.LoginService;
import com.sim.common.entity.UserLoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Base64;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/in", method = RequestMethod.POST)
    public String login(HttpServletRequest request) {
        String name = request.getParameter("name");

        try {
            //String password = new String(Base64.getDecoder().decode(request.getParameter("password").getBytes("UTF-8")));

            //loginService.login(name,password);
            HttpSession session = (HttpSession) request.getAttribute("org.springframework.session.SessionRepository.CURRENT_SESSION");
            UserLoginEntity loginUser = new UserLoginEntity();
            loginUser.setUserId(name);
            loginUser.setUserName(name);
            session.setAttribute("LOGINSUSER",loginUser);

            return "success";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

}
