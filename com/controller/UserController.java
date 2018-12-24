package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ConfigurableWebApplicationContext;

@RestController
public class UserController {

    @Autowired
    ApplicationContext context;

    @RequestMapping("a")
    String a(Integer id) {
        System.out.println(id);
        ApplicationContext parent = context.getParent();
        return "a";
    }


}
