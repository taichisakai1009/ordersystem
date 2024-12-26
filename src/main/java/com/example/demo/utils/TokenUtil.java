package com.example.demo.utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component // このクラスはSpringのコンポーネントとして管理されることを示す
public class TokenUtil {

    private static final String HMAC_ALGO = "HmacSHA256"; // 使用するハッシュアルゴリズム（HMAC-SHA256）

    // トークンを生成するメソッド
    // data: トークンに含めるデータ
    // secretKey: トークンの署名に使用するシークレットキー
    // 戻り値: 生成されたBase64URLエンコードされたトークン
    public String generateToken(String data, String secretKey) throws Exception {
        // HMAC-SHA256アルゴリズムを使用するMacインスタンスを取得
        Mac mac = Mac.getInstance(HMAC_ALGO);
        
        // シークレットキーをSecretKeySpecオブジェクトとして設定
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
        
        // シークレットキーでMacインスタンスを初期化
        mac.init(secretKeySpec);
        
        // 入力データ（data）をHMACでハッシュ化
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // ハッシュ化した結果をBase64URLエンコード（パディングなし）してトークンとして返す
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    // シークレットキーを生成するメソッド
    // 戻り値: Base64URLエンコードされた新しいシークレットキー
    public String generateSecretKey() {
        // "secret_"と現在のシステム時刻を組み合わせた文字列を生成
        String key = "secret_" + System.currentTimeMillis();
        
        // 生成したキーをUTF-8でエンコードし、Base64URLエンコードして返す
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }

    // トークンの有効期限をチェックするメソッド
    // expiresAt: トークンの有効期限
    // 戻り値: トークンが期限切れの場合はtrue、まだ有効な場合はfalse
    public boolean isTokenExpired(LocalDateTime expiresAt) {
        // 現在時刻がexpiresAtを過ぎているかどうかをチェック
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
