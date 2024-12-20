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
		if (clerk != null) {
			if (clerk.getIsFirstLogin()) { // 初回ログインの場合
				// セッションにユーザー名を保存
		        HttpSession session = request.getSession();
		        session.setAttribute("authenticatedUsername", username);
		        session.setAttribute("emailAddress", clerk.getMailAddress());
//				clerk.setIsFirstLogin(false);
//				clerksRepository.save(clerk);
//				&username=" + URLEncoder.encode(username, "UTF-8")
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
