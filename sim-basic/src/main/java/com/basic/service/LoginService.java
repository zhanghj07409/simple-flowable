package com.basic.service;

import org.springframework.stereotype.Service;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Service
public class LoginService {


    public void login(String name,String password) throws Exception{
        if(!name.equals("admin")){
            throw new Exception("用户名错误");
        }else if(!password.equals("test")){
            throw new Exception("密码错误");
        }
    }
}
