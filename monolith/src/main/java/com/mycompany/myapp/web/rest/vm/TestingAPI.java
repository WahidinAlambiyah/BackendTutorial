package com.mycompany.myapp.web.rest.vm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testapi/testing")
public class TestingAPI {

    @GetMapping(value = "")
    public String test() {
        return "OK";
    }
    
}
