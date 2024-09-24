package com.example.springSecuritydemo;

import com.example.springSecuritydemo.jwt.JwtUtils;
import com.example.springSecuritydemo.jwt.LoginRequest;
import com.example.springSecuritydemo.jwt.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GreetingsController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
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

    @PostMapping("/signin")
    public ResponseEntity<?>authenticateUser(@RequestBody LoginRequest loginRequest)
    {
        Authentication authentication;
        try{
            authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                    ,loginRequest.getPassword()));
        }catch (AuthenticationException e){
            Map<String,Object> map=new HashMap<>();
            map.put("message","Bad credentials");
            map.put("status",false);
            return  new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        String jwtToken= jwtUtils.generateTokenFromUserName(userDetails);

        List<String> roles=userDetails.getAuthorities().stream()
                .map(item->item.getAuthority())
                .toList();

        LoginResponse response=new LoginResponse(jwtToken,userDetails.getUsername(),roles);
        return ResponseEntity.ok(response);

    }

}
