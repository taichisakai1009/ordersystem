package com.example.demo.TwoFactorAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.ClerksService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

// TotpService と QRCodeGenerator を統合します。
// 2要素認証用のQRコードを生成してクライアントに提供するコントローラクラスです。

@Controller
@RequestMapping("/two-factor-auth")
public class TwoFactorAuthController {

	@Autowired
	private TotpService totpService; // 2要素認証に必要なシークレットキー生成とQRコードURL作成サービス
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	@Autowired
	private ClerksService clerksservice;

	/**
	 * QRコード画像を生成して返すエンドポイント
	 * 
	 * @param model Spring MVCのModelオブジェクト（今回は未使用）
	 * @return ResponseEntity<byte[]> QRコード画像のバイナリデータを返すレスポンス
	 * @throws MessagingException 
	 */
	// 	このエンドポイントがボタンを押下で呼び出されていた。
	//	@GetMapping("/qr-code") // HTTP GETリクエストでこのエンドポイントを呼び出す
	/*
	 * ResponseEntity<String> の代わりに、ResponseEntity<byte[]> を返します。
	 * これにより、Base64エンコーディングされた文字列ではなく、生のバイナリデータをクライアントに返すことができます。
	 */
	//	public ResponseEntity<byte[]> getQrCode(HttpSession session) throws MessagingException {
	//
	//		return twoFactorAuthService.getQrCode(session);
	//	}

	@GetMapping("/verification") // HTTP GETリクエストでこのエンドポイントを呼び出す
	public String verification(String code, Model model, HttpSession session) {
		boolean verifyFlg = twoFactorAuthService.verification(code, session);
		if (verifyFlg) {
			String username = (String) session.getAttribute("authenticatedUsername");
			Integer clerkNumber = Integer.parseInt(username);
			clerksservice.addClerkModel(clerkNumber, model);
			clerksservice.updateIsFirstLoginToFalseByClerkNumber(clerkNumber); // 初回ログインフラグをオフ
			return "clerks/home";
		} else {
			model.addAttribute("verifyError", "認証コードが正しくありません。");
			return "login/twoStepVerification";
		}
	}
}
