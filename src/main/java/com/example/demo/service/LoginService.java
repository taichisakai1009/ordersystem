package com.example.demo.service;

import java.net.URLEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.utils.PasswordUtils;
import com.example.demo.utils.RSAUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {

	private final ClerksRepository clerksRepository;

	private final PasswordEncoder passwordEncoder;

	private final TokenService tokenService;

	public LoginService(ClerksRepository clerksRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
		this.clerksRepository = clerksRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
		System.out.println("tokenServiceのインスタンス："+tokenService);
		System.out.println("LoginServiceのコンストラクタ呼び出し");
	}

	// ログイン判定 詳細なエラーが出てしまうので非推奨
	//	public boolean doLogin(Integer clerkNumber, String password) throws NoSuchAlgorithmException {
	//		String hashedPassword = hash(password);
	//		boolean loginFlg = clerksRepository
	//				.findByClerkNumberAndPassword(clerkNumber, hashedPassword).isPresent();
	//		return loginFlg;Optional<ClerksEntity>
	//	}
	
    @PostConstruct
    public void init() {
        System.out.println("LoginService インスタンスが作成されました");
    }

	public boolean doLogin(Integer clerkNumber, String password) {
		// 社員番号から検索
		ClerksEntity clerksEntity = clerksRepository.findByClerkNumber(clerkNumber);
		if (clerksEntity == null) {
			return false;
		}

		String hashPassword = clerksEntity.getPassword();
		// エンコードされたパスワードと入力されたパスワードを比較
		return PasswordUtils.matchPassword(password, hashPassword);
	}

	public boolean resetPassword(String newPassword, String confirmNewPassword, HttpSession session, Model model) {
		boolean resetPasswordFlg = true;
		if (!newPassword.equals(confirmNewPassword)) {
			System.out.println("確認用パスワードと一致しません。");
			String mismatchError = "確認用パスワードと一致しません。";
			model.addAttribute("mismatchError", mismatchError);
			resetPasswordFlg = false;
		} else {
			Integer clerkId = (Integer) session.getAttribute("clerkId");
			String hashPassword = passwordEncoder.encode(newPassword);
			clerksRepository.updatePasswordByClerkId(hashPassword, clerkId);

			String resetConfirm = "パスワード再設定が完了しました。";
			model.addAttribute("resetConfirm", resetConfirm);
		}
		return resetPasswordFlg;
	}

	// 店員情報と制限時間からリンクを作成
	public String createResetLink(ClerksEntity clerk, int tokenLimitMinute) throws Exception {
		String token = tokenService.createToken(clerk, tokenLimitMinute);
		String encryptedToken = RSAUtil.encrypt(token);

		encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8"); // URLに送付するのでURLエンコード

		return "http://localhost:8080/login/resetPassword?show&encryptedToken=" + encryptedToken;
	}
}
