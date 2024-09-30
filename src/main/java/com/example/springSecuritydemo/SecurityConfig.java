package com.example.springSecuritydemo;

import com.example.springSecuritydemo.jwt.AuthEntryPointJwt;
import com.example.springSecuritydemo.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    DataSource dataSource;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) ->
           requests.requestMatchers("/h2-console/**").permitAll()
                   .requestMatchers("/signin").permitAll()
                   .anyRequest().authenticated());


//        to make authentication stateless
        http.sessionManagement(session->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler));

//        http.formLogin(Customizer.withDefaults());// this will enable form based authentication
//        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable());//to disable bar bar username pass puch rha tha in h2 console
        http.headers(headers->
                headers.frameOptions(frameOptions->frameOptions.sameOrigin()));//to properly show h2-db in browser

        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return (SecurityFilterChain)http.build();
    }

//    This method had to be commented because it was getting problem as the data was getting saved into
//    the db at the same time when the db was getting configured
//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails user1= User.withUsername("user1")
//                .password(passwordEncoder().encode("Sachin@123"))
//                .roles("USER")
//                .build();
//        UserDetails admin= User.withUsername("admin")
//                .password(passwordEncoder().encode("adminPass"))
//                .roles("ADMIN")
//                .build();
//
//        //Saving user to h2DB
//        JdbcUserDetailsManager userDetailsManager=new JdbcUserDetailsManager(dataSource);
//        userDetailsManager.createUser(user1);
//        userDetailsManager.createUser(admin);
//        return userDetailsManager;
////        return new InMemoryUserDetailsManager(user1,admin);   this will be returned while creating in memory users
//    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource)
    {
        return new JdbcUserDetailsManager(dataSource);
    }
    @Bean
    public CommandLineRunner initData(UserDetailsService userDetailsService)
    {
        return args->{
            JdbcUserDetailsManager manager=(JdbcUserDetailsManager) userDetailsService;
            UserDetails user1= User.withUsername("user1")
                .password(passwordEncoder().encode("Sachin@123"))
                .roles("USER")
                .build();
        UserDetails admin= User.withUsername("admin")
                .password(passwordEncoder().encode("adminPass"))
                .roles("ADMIN")
                .build();
            JdbcUserDetailsManager userDetailsManager=new JdbcUserDetailsManager(dataSource);
        userDetailsManager.createUser(user1);
        userDetailsManager.createUser(admin);

//        return new InMemoryUserDetailsManager(user1,admin);   this will be returned while creating in memory users

        };
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder)
            throws Exception
    {
        return builder.getAuthenticationManager();
    }

}
