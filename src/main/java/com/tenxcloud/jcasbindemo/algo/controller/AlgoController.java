package com.tenxcloud.jcasbindemo.algo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/data-service/algo")
public class AlgoController {
    @RequestMapping(value = "/role/per",method = RequestMethod.GET)
    public String addPer(@RequestParam String name){
        return "success";
    }
}
