package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;

import com.example.demo.TwoFactorAuth.TwoFactorAuthController;
import com.example.demo.TwoFactorAuth.TwoFactorAuthService;
import com.example.demo.service.ChoiceService;
import com.example.demo.service.EmailService;
import com.example.demo.service.LoginService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private ChoiceService choiceService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private TwoFactorAuthController twoFactorAuthController;
	
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;

	// ログイン判定(SplingSecurityの実装をしたら必要ない)
	//	@RequestMapping(path = "/login", params = "login")
	//	public String doLogin(Integer clerkNumber, String password) throws NoSuchAlgorithmException {
	//		boolean loginFlg = loginService.doLogin(clerkNumber, password);
	//		if (loginFlg) {
	//			return "test/test";
	//		} else {
	//			return "login/login";
	//		}
	//	}

	// ログイン画面の表示
	@RequestMapping(path = "/login", params = "show")
	public String showLoginView() {
		return "login/login";
	}
	
	// 二段階認証画面の表示
	@RequestMapping(path = "/twoStepVerification", params = "show")
	public String showVerificationPage(HttpSession session) throws MessagingException {
		// QRコードのメール送信処理
		twoFactorAuthService.getQrCode(session);

		return "login/twoStepVerification";
	}
			
	// ログアウト成功
	@RequestMapping(path = "/login", params = "logout")
	public String logout(HttpSession session) {
//		choiceService.restOrderRecord(1, session);
		return "title/title";
	}

	// 注文画面に戻る
	@RequestMapping(path = "/login", params = "back")
	public String back() {
		System.out.println("注文画面に戻る");
		return "order/choice";
	}
}