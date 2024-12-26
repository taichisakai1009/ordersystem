package com.example.demo.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component // このクラスはSpringのコンポーネントとして管理されることを示す
public class HashUtil {
	public String hashStr(String str) throws NoSuchAlgorithmException {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
	    return Base64.getEncoder().encodeToString(hash);
	}
}
