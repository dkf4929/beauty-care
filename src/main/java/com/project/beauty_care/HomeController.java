package com.project.beauty_care;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello()
    {
        System.out.println("HomeController.hello");
        return "hello";
    }
}
