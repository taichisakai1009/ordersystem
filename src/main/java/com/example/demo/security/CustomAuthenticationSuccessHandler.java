package com.example.demo.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final ClerksRepository clerksRepository;

	CustomAuthenticationSuccessHandler(ClerksRepository clerksRepository) {
		this.clerksRepository = clerksRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// ユーザー情報を取得
		UserDetails user = (UserDetails) authentication.getPrincipal();
		String username = user.getUsername();
		Integer clerkNumber = Integer.parseInt(username); // 店員番号が文字になってるのでIntegerに変換
		ClerksEntity clerk = clerksRepository.findByClerkNumber(clerkNumber);
		boolean isFirstLogin = clerk.getIsFirstLogin();// 現在の認証情報を取得
		
		if (clerk != null) {
			if (isFirstLogin) { // 初回ログインの場合
				System.out.println("初回ログイン");
				
		        HttpSession loginSession = request.getSession();
		        loginSession.invalidate(); // セッションを無効化、ログイン情報の解除
		        
		        HttpSession session = request.getSession(); // この新しいセッションは、前のセッションとは異なるセッションIDを持っています
		        
		        session.setAttribute("authenticatedUsername", username);
		        session.setAttribute("isFirstLogin", isFirstLogin);
		        session.setAttribute("emailAddress", clerk.getMailAddress());
				response.sendRedirect("/login/twoStepVerification?show");
			} else {
				response.sendRedirect("/clerks/home");
			}
			// 権限を確認
			//			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"))) {
			//				response.sendRedirect("/clerks/home");
			//			} 
		}
	}
}
