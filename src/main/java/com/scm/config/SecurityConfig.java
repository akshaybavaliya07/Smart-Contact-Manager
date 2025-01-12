package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private OauthAuthenticationSuccessHandler successHandler;

    @Autowired
    private OauthAuthenticationFailureHandler failureHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> 
            auth
                .requestMatchers("/forgot-password", "/reset-password", "/api/user/**").permitAll()
                .requestMatchers("/user/**").authenticated()
                .anyRequest().permitAll()
        );

        httpSecurity.formLogin(formLogin -> {
            formLogin
            .loginPage("/login")
            .loginProcessingUrl("/authenticate")
            .defaultSuccessUrl("/user/profile", true)
            .usernameParameter("email")
            .passwordParameter("password");

            formLogin.failureHandler(failureHandler);
        });

        httpSecurity.oauth2Login(oauth -> {
            oauth
            .loginPage("/login")
            .successHandler(successHandler);
        });

        httpSecurity.csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}