package com.example.demo.TwoFactorAuth;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import de.taimos.totp.TOTP; // TOTPライブラリ (de.taimos.totp) を使用

/**
 * TOTP (Time-based One-Time Password) のサービスクラス
 * 
 * - シークレットキーの生成
 * - QRコードURLの生成
 * - TOTPコードの検証
 */
@Service // Springのサービスコンポーネントとしてマーク
public class TotpService {
	
	 /**
     * シークレットキーを生成するメソッド（RFC 6238準拠）
     * 
     * @return Base32エンコードされたシークレットキー
     */
    public String generateSecretKey() {
        try {
            // 128ビットのランダムなシークレットキーを生成
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16]; // 16バイト = 128ビット
            random.nextBytes(key);

            // Base32でエンコード
            Base32 base32 = new Base32();
            return base32.encodeToString(key);
        } catch (Exception e) {
            // キー生成中に例外が発生した場合はRuntimeExceptionとしてスロー
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }

    /**
     * TOTP用のQRコードURLを生成するメソッド
     * 
     * @param username   ユーザー名 (ユーザーの識別情報)
     * @param secretKey  シークレットキー
     * @return QRコード生成に使用するotpauthプロトコル形式のURL
     */
    public String getQRCodeUrl(String username, String secretKey) {
        // 発行者名（アプリケーション名）
        String issuer = "OrderSystem"; // Google Authenticatorなどに表示されるアプリ名

        // otpauth形式のURLを生成 (RFC 6238に基づく)
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer, username, secretKey, issuer
        );
    }
    
    /**
     * TOTPコードを検証するメソッド
     * 
     * @param secretKey  Base32エンコードされたシークレットキー
     * @param code       ユーザーが入力したTOTPコード
     * @return 入力されたコードが現在のTOTPコードと一致すればtrue、そうでなければfalse
     */
    public boolean verifyCode(String secretKey, String code) {
        try {
            // Base32デコード
            Base32 base32 = new Base32();
            byte[] decodedKey = base32.decode(secretKey);
            
            // デコードされたシークレットキーを16進数文字列に変換
            StringBuilder hexKey = new StringBuilder();
            for (byte b : decodedKey) {
                hexKey.append(String.format("%02x", b)); // 2桁の16進数形式に変換
            }
            
//            System.out.println("デコードされたシークレットキー（16進数）：" + hexKey.toString());

            // 現在のTOTPコードを計算 (TOTPライブラリを使用)
            String currentCode = TOTP.getOTP(hexKey.toString());

//            System.out.println("現在のTOTPコード：" + currentCode);
//            System.out.println("入力コード：" + code);

            // 現在のコードとユーザー入力のコードを比較
            return currentCode.equals(code);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }    
}
