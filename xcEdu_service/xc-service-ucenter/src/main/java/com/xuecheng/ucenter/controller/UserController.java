package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UcenterControllerApi;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ucenter")
public class UserController implements UcenterControllerApi {


    @Autowired
    private UserService userService;

    @Override
    @GetMapping("getuserext")
    public XcUserExt findByUsername(@RequestParam("username") String username) {
        return userService.findByUsername(username);
    }
}
