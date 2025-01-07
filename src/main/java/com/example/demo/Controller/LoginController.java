package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.TwoFactorAuth.TwoFactorAuthService;
import com.example.demo.service.ClerksService;
import com.example.demo.service.EmailService;
import com.example.demo.service.LoginService;
import com.example.demo.service.TokenService;
import com.example.demo.service.test;
import com.example.demo.utils.HashUtil;
import com.example.demo.utils.RSAUtil;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

	private final LoginService loginService;

	private final EmailService emailService;

	private final ClerksService clerksService;

	private final TokenService tokenService;

	private final TwoFactorAuthService twoFactorAuthService;

	private final HashUtil hashUtil;
	
//	private final PrintWriter printWriter;

	@Autowired
	private test test;

	LoginController(LoginService loginService, EmailService emailService, ClerksService clerksService,
			TokenService tokenService, TwoFactorAuthService twoFactorAuthService, HashUtil hashUtil) {
		this.loginService = loginService;
		this.emailService = emailService;
		this.clerksService = clerksService;
		this.tokenService = tokenService;
		this.twoFactorAuthService = twoFactorAuthService;
		this.hashUtil = hashUtil;
		System.out.println("loginServiceのインスタンス:" + loginService);
		System.out.println("LoginControllerのコンストラクタ呼び出し");
		System.out.println("フィールドインジェクション、依存関係の注入前：" + test);
	}

	// リンクの有効期限を示す定数(分)
	private static final int RESET_LINK_EXPIRATION_MINUTES = 1;

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

	// 依存関係の注入が終わった直後に呼び出される
	@PostConstruct
	public void init() {
		System.out.println("LoginController インスタンスが作成されました");
		System.out.println("フィールドインジェクション、依存関係の注入後：" + test);
	}

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
		ClerksEntity clerk = clerksService.findByMailaddress(email); // メルアドから店員情報を取得
		if (clerk == null) {
			model.addAttribute("eMailNotExist", "指定されたメールアドレスは登録されていません。");
			return "login/sendEmail";
		} else {
			String resetLink = loginService.createResetLink(clerk, RESET_LINK_EXPIRATION_MINUTES); // リンク設定
			emailService.sendSimpleMessage(email, "パスワード再設定用リンク", resetLink); // メール送信

			String sendMailConfirm = "指定されたメールアドレスにパスワード再設定用リンクを送付しました。<br>"
					+ RESET_LINK_EXPIRATION_MINUTES + "分以内にリンクからアクセスしてください。"; // 確認メッセージ
			model.addAttribute("sendMailConfirm", sendMailConfirm); // 確認メッセージ
			return "login/sendEmail";
		}
	}

	// パスワード再設定用画面の表示
	@RequestMapping(path = "/resetPassword", params = "show")
	public String showResetPage(String encryptedToken, HttpSession session) throws Exception {
		String token = RSAUtil.decrypt(encryptedToken); // 複合化
		String hashToken = hashUtil.hashStr(token); // 	ハッシュ化
		Integer clerkId = tokenService.getClerkIdByToken(hashToken); // トークンから店員ID取得
		boolean validateTokenFlg = tokenService.validateToken(hashToken);
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