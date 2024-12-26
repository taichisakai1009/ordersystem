package com.example.demo.Controller;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.TwoFactorAuth.TwoFactorAuthService;
import com.example.demo.service.ChoiceService;
import com.example.demo.service.ClerksService;
import com.example.demo.service.EmailService;
import com.example.demo.service.LoginService;
import com.example.demo.service.TokenService;
import com.example.demo.utils.HashUtil;
import com.example.demo.utils.RSAUtil;

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
	private TokenService tokenService;

	@Autowired
	private TwoFactorAuthService twoFactorAuthService;

	@Autowired
	private HashUtil hashUtil;

//	// ログイン判定(SplingSecurityの実装をしたら必要ない)
//		@RequestMapping(path = "/login", params = "login")
//		public String doLogin(Integer clerkNumber, String password) throws NoSuchAlgorithmException {
//			boolean loginFlg = loginService.doLogin(clerkNumber, password);
//			if (loginFlg) {
//				return "test/test";
//			} else {
//				return "login/login";
//			}
//		}

	// ログイン画面の表示
	@RequestMapping(path = "/login", params = "show")
	public String showLoginView() throws Exception {
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
	public String sendResetPassEmail(String email, HttpSession session, Model model) throws Exception {
		ClerksEntity clerk = clerksService.findByMailaddress(email);
		if (clerk == null) {
			model.addAttribute("eMailNotExist", "指定されたメールアドレスは登録されていません。");
			return "login/sendEmail";
		} else {
			int tokenLimitMinute = 1; // トークンの時間設定(分)
			String token = tokenService.createToken(clerk, tokenLimitMinute);
			String encryptedToken = RSAUtil.encrypt(token);
			System.out.println("生のトークン：" + token + "文字数:" + token.length());

			encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8"); // URLに送付するのでURLエンコード
	
			Integer clerkId = clerk.getClerkId();
			String resetLink = "http://localhost:8080/login/resetPassword?show&encryptedToken=" + encryptedToken
					+ "&clerkId=" + clerkId;
			emailService.sendSimpleMessage(email, "パスワード再設定用リンク", resetLink);
			model.addAttribute("sendMailConfirm", "指定されたメールアドレスにパスワード再設定用リンクを送付しました。<br>"
					+ tokenLimitMinute + "分以内にリンクからアクセスしてください。");
			return "login/sendEmail";
		}
	}

	// パスワード再設定用画面の表示
	@RequestMapping(path = "/resetPassword", params = "show")
	public String showResetPage(String encryptedToken, Integer clerkId, HttpSession session) throws Exception {
		String token = RSAUtil.decrypt(encryptedToken);
		System.out.println("復号トークン：" + token + "文字数:" + token.length());
		String hashToken = hashUtil.hashStr(token); // 	ハッシュ化
		boolean validateTokenFlg = tokenService.validateToken(hashToken); // 一旦生のハッシュ値を入れないと判定できない方が、強力？
		System.out.println("トークン認証：" + validateTokenFlg + "、店員ID:" + clerkId);
		if (validateTokenFlg) {
			session.setAttribute("clerkId", clerkId);
			return "login/resetPassword";
		} else {
			return "error/error";
		}
	}

	// パスワード再設定
	@RequestMapping(path = "/resetPassword", params = "reset")
	public String resetPassword(String newPassword, String confirmNewPassword, HttpSession session, Model model) {
		loginService.resetPassword(newPassword, confirmNewPassword, session, model);
		//		session.removeAttribute("email");
		return "login/resetPassword";
	}
}