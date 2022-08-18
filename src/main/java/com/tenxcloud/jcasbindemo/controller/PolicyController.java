package com.tenxcloud.jcasbindemo.controller;

import com.tenxcloud.jcasbindemo.config.EnforcerFactory;
import com.tenxcloud.jcasbindemo.entry.Policy;
import org.springframework.web.bind.annotation.*;

@RestController
public class PolicyController {

    @PutMapping("/anon/role/per")
    public String addPer(){

        EnforcerFactory.addPolicy(new Policy("alice", "/user/list", "GET"));

        return "success";
    }

    @DeleteMapping("/anon/role/per")
    public String deletePer() {

        EnforcerFactory.removePolicy(new Policy("alice", "/user/list", "*"));
        return "success";
    }

    @GetMapping("/user/list")
    public String getPer(@RequestParam String name) {

        System.out.println(name);
        return "success";
    }
}
