package com.example.demo.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 権限を確認
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"))) {
            response.sendRedirect("/clerks/home");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Manager"))) {
            response.sendRedirect("/clerks/home");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Regular"))) {
            response.sendRedirect("/clerks/home");
        } else {
            response.sendRedirect("/login/login?error=unauthorized"); // 権限がない場合の処理
        }
    }
}

