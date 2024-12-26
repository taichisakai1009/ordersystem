//package com.example.demo.utils;
//
//import java.util.Base64;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.springframework.stereotype.Component;
//
//@Component // このクラスはSpringのコンポーネントとして管理されることを示す
//public class AESUtil {
//
//    // 固定のAESキーを設定（16バイトのキー、128ビット）
//    // このキーはAES暗号化/復号化に使用される固定の鍵です。
//    private static final String FIXED_KEY = "1234567890123456"; // 16文字で128ビットのキー（任意の16文字）
//
//    // 固定のAESキーをSecretKeySpecとして作成
//    // "AES"アルゴリズムに対応した秘密鍵を作成
//    private static SecretKeySpec createFixedKey() {
//        // FIXED_KEYの文字列をバイト配列に変換し、AESアルゴリズム用にSecretKeySpecを生成
//        return new SecretKeySpec(FIXED_KEY.getBytes(), "AES");
//    }
//
//    // 文字列の暗号化（固定のキーを使用）
//    // 引数で渡された文字列をAESで暗号化し、Base64でエンコードして返す
//    public static String encrypt(String data) throws Exception {
//        // 固定のキーでSecretKeySpecを作成
//        SecretKeySpec secretKeySpec = createFixedKey();
//        
//        // AES暗号化アルゴリズム、ECBモード、PKCS5Paddingを使用
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        
//        // 暗号化モードに初期化
//        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//        
//        // データをバイト配列に変換して暗号化
//        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
//        System.out.println("エンコード前：" + encryptedBytes);
//        
//        // 暗号化されたバイト配列をBase64エンコードして返す
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }
//
//    // 文字列の復号化（固定のキーを使用）
//    // 暗号化された文字列（Base64エンコードされたもの）を復号化して返す
//    public static String decrypt(String encryptedData) throws Exception {
//        // 固定のキーでSecretKeySpecを作成
//        SecretKeySpec secretKeySpec = createFixedKey();
//        
//        // AES暗号化アルゴリズム、ECBモード、PKCS5Paddingを使用
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        
//        // 復号化モードに初期化
//        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//        
//        // Base64でエンコードされた暗号化されたデータをデコード
//        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
//        
//        // 復号化して元のデータに戻す
//        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
//        
//        // 復号化されたバイト配列を文字列に変換して返す
//        return new String(decryptedBytes);
//    }
//}
