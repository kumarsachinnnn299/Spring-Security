package com.example.springSecuritydemo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {

@GetMapping("/hello")//This endpoint is accssible to all users
    public String sayHello(){
        return "Hello";
    }
    @GetMapping("/user")//This endpoint is accssible to normal user only
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint(){
        return "Hello User!!";
    }
    @GetMapping("/admin")//This endpoint is accssible to admin users only
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint(){
        return "Hello Admin!!!";
    }

}
