package com.cubic.viedo.controller;

import com.alibaba.fastjson.JSON;
import com.cubic.viedo.module.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class LoginController {

    @RequestMapping("/valid")
    public String valid(@RequestBody String data){
        ResponseBody<String> responseBody = new ResponseBody<>(0,"admin-token");
        return JSON.toJSONString(responseBody);
    }

    @RequestMapping("/logout")
    public String logout(){
        ResponseBody<String> responseBody = new ResponseBody<>(0,"success");
        return JSON.toJSONString(responseBody);
    }

    @RequestMapping("/info")
    public String info(){
        Map<String,Object> info = new HashMap<>();
        info.put("roles",new String[]{"admin"});
        info.put("introduction"," am a super administrator");
        info.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        info.put("name","Super Admin");

        ResponseBody<Map> responseBody = new ResponseBody<>(0,info);
        return JSON.toJSONString(responseBody);
    }
}
