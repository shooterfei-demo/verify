package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/aiems")
public class AiemsController {

    @GetMapping("/test")
    private Map<String, Object> test() {

        Map<String, Object> result = new HashMap<>();
        result.put("ret", "success");
        return result;
    }
}
