package com.example.demo.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.utils.PasswordUtils;

import jakarta.servlet.http.HttpSession;

@Service
public class LoginService {

	@Autowired
	private ClerksRepository clerksRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// SHA-256 ハッシュ化
	public static String hash(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());
		byte[] hashBytes = md.digest();
		String hash = Base64.getEncoder().encodeToString(hashBytes);
		System.out.println("Hashed Password: " + hash);
		return hash;
	}

	// ログイン判定 詳細なエラーが出てしまうので非推奨
	//	public boolean doLogin(Integer clerkNumber, String password) throws NoSuchAlgorithmException {
	//		String hashedPassword = hash(password);
	//		boolean loginFlg = clerksRepository
	//				.findByClerkNumberAndPassword(clerkNumber, hashedPassword).isPresent();
	//		return loginFlg;Optional<ClerksEntity>
	//	}

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
			String email = (String) session.getAttribute("email"); // null
			System.out.println("パスワードを再設定します。email=" + email);
			
			String hashPassword = passwordEncoder.encode(newPassword);
			clerksRepository.updatePasswordByMailAddress(hashPassword, email);

			String resetConfirm = "パスワード再設定が完了しました。";
			model.addAttribute("resetConfirm", resetConfirm);
		}
		return resetPasswordFlg;
	}
}
