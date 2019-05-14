package com.basic.service;

import org.springframework.stereotype.Service;

/**
 * @author: zhanghj
 * @date: 2018/6/5
 * @description :
 * @version: 2.0
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
