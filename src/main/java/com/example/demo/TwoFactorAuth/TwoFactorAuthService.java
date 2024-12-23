package com.example.demo.TwoFactorAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.security.ClerkDetails;
import com.example.demo.service.EmailService;
import com.example.demo.service.PdfService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Service
public class TwoFactorAuthService {

	@Autowired
	private TotpService totpService; // 2要素認証に必要なシークレットキー生成とQRコードURL作成サービス

	@Autowired
	private EmailService emailService;

	@Autowired
	private PdfService pdfService;

	public void getQrCode(HttpSession session) throws MessagingException {

		// サーバー側セッションからユーザー名を取得
		String username = (String) session.getAttribute("authenticatedUsername");

		if (username == null) {
			throw new IllegalStateException("ユーザーが認証されていません");
		}

		// シークレットキーを生成
		String secretKey = totpService.generateSecretKey();

		session.setAttribute("secretKey", secretKey); // シークレットキーをセッション追加

		// シークレットキーとユーザー名を基にQRコードURLを生成
		String qrCodeUrl = totpService.getQRCodeUrl(username, secretKey);

		// QRコードURLを基にQRコード画像のバイナリデータを生成
		byte[] qrCodeBytes = QRCodeGenerator.generateQRCodeImage(qrCodeUrl, 300, 300);

		// PDF形式のバイナリデータに変換
		byte[] qrCodePdfBytes = pdfService.sendByteAsPdf(qrCodeBytes);

		// メールアドレスを取得
		String emailAddress = (String) session.getAttribute("emailAddress");
		// sakait@analix.co.jp
		// メール送信
		emailService.sendFileMessage(emailAddress,
				"確認コード用QRコードの告知",
				"以下のQRコードを認証アプリでスキャンしてください。",
				"QRCode.pdf", // ファイル名の形式が正しくないと表示されない
				qrCodePdfBytes);
	}

	public boolean verification(String code, HttpSession session) {

		String secretKey = (String) session.getAttribute("secretKey");
		if (secretKey == null) {
			System.out.println("シークレットキーが取得できませんでした。");
			return false;
		}
		boolean verifyFlg = totpService.verifyCode(secretKey, code);
		//		System.out.println("認証結果：" + verifyFlg);
		//		session.removeAttribute("secretKey");
		return verifyFlg;
	}
	
	public void setLoginByClerks(ClerksEntity clerk, HttpSession session) {
        ClerkDetails clerkDetails = new ClerkDetails(clerk); // ユーザー情報の取得
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
        		clerkDetails, null, clerkDetails.getAuthorities()); // ログイン情報の再設定
        SecurityContextHolder.getContext().setAuthentication(newAuthentication); // ログイン情報の保存
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        		, SecurityContextHolder.getContext()); // セッションに SecurityContext を保存
	}

}
