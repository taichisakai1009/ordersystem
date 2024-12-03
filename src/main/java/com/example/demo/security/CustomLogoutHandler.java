//package com.example.demo.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//public class CustomLogoutHandler implements LogoutHandler {
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        HttpSession session = request.getSession(false); // 現在のセッションを取得
//        if (session != null) {
//            // 必要に応じてセッション情報を保持
//            System.out.println("セッション情報を保持します。Session ID: " + session.getId());
//        }
//
//        // セッションを無効化しないため、デフォルトのセッション無効化処理は呼びません
//    }
//}