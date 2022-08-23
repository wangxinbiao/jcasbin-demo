package com.tenxcloud.jcasbindemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tenxcloud.jcasbindemo.config.EnforcerFactory;
import com.tenxcloud.jcasbindemo.entry.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@RestController
@RequestMapping("/api/v1/data-service")
public class PolicyController {

    @PutMapping("/anon/role/per")
    public String addPer(){

        EnforcerFactory.addPolicy(new Policy("alice", "/user/list/{id}", "GET"));

        return "success";
    }

    @PutMapping("/anon/role/group")
    public String addPer02(){

        EnforcerFactory.updatePolicy(new Policy("alice", "/user/list", "GET"));

        return "success";
    }

    @DeleteMapping("/anon/role/per")
    public String deletePer() {

        EnforcerFactory.removePolicy(new Policy("alice", "/user/list", "*"));
        return "success";
    }

    @GetMapping("/user/{id}/{nodeId}")
    public String getPer(@RequestParam String name,@PathVariable String id,@PathVariable String nodeId) {

        System.out.println(name);
        return "success";
    }
}
