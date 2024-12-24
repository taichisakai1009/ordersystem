package com.example.demo.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;

import com.example.demo.TwoFactorAuth.TwoFactorAuthService;
import com.example.demo.service.ChoiceService;
import com.example.demo.service.ClerksService;
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
	private ClerksService clerksService;

	@Autowired
	private TemplateEngine templateEngine;

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

	// パスワードを忘れた人
	@RequestMapping(path = "/sendEmail", params = "show")
	public String showSendEmailPage() {
		return "login/sendEmail";
	}

	// パスワード再設定用のリンクを載せたメールを送信
	@RequestMapping(path = "/sendEmail", params = "send")
	public String sendResetPassEmail(String email, HttpSession session ,Model model) throws MessagingException, UnsupportedEncodingException {
		boolean emailExist = clerksService.existsByMailAddress(email);
		if (!emailExist) {
			model.addAttribute("eMailNotExist", "指定されたメールアドレスは登録されていません。");
			return "login/sendEmail";
		} else {
			String resetLink = "http://localhost:8080/login/resetPassword?show&email=" + URLEncoder.encode(email, "UTF-8");
			emailService.sendSimpleMessage(email, "パスワード再設定用リンク", resetLink);
//			session.setAttribute("email", email);
//			String sessionEmail = (String) session.getAttribute("email");
//			System.out.println("sessionEmail:"+sessionEmail);
//			emailService.sendSimpleMessage(email, "パスワード再設定用リンク", "http://localhost:8080/login/resetPassword?show");
			System.out.println(email + "へメール送信");
			return "login/sendEmail";
		}
	}
	
	// パスワード再設定用画面の表示
	@RequestMapping(path = "/resetPassword", params = "show")
	public String showResetPage(String email,HttpSession session) {
		session.setAttribute("email", email);
		String sessionEmail = (String) session.getAttribute("email");
		System.out.println("sessionEmail:"+sessionEmail);
		return "login/resetPassword";
	}
	
//	// パスワード再設定用画面の表示
//	@RequestMapping(path = "/resetPassword", params = "show")
//	public String showResetPage() {
//		return "login/resetPassword";
//	}
	
	// パスワード再設定
	@RequestMapping(path = "/resetPassword", params = "reset")
	public String resetPassword(String newPassword, String confirmNewPassword, HttpSession session ,Model model) {
		loginService.resetPassword(newPassword, confirmNewPassword, session, model);
//		session.removeAttribute("email");
		return "login/resetPassword";
	}
}