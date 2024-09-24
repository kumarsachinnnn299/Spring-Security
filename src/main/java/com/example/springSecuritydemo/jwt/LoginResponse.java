package com.example.springSecuritydemo.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponse {
    private String jwtToken;
    private String username;
    private List<String>roles;
}
