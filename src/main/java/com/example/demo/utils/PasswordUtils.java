package com.example.demo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
	// BCryptPasswordEncoderのインスタンスを作成
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // パスワードをハッシュ化するメソッド
    public static String password(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // パスワードを検証するメソッド
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
