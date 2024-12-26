package com.example.demo.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

public class RSAUtil {

	// 固定された秘密鍵と公開鍵（Base64でエンコード）
	private static final String FIXED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs+FdGMOURp7VJg72OjPBXItD6+L9IGQ46ZOFLBKWrABjVCjKDGnzbtSI+fzLO2e35CsrAcxld3nTg6pG+NhUepFF7996Du/RnksxP6VF+8+AIwgElkfXd2swM/X7un2jaj3H7bk3f7Uw4iNstp7TdhedqLgKDMP6kG8yZp2AzulEZhg+HsdO8C5+hOBqY0oDTD2rSWBTKUjL9LCuNMS/Cst8vHc37kQWSSe7X49xgrqE0bb/R+XkbSc0nivIRLQgdI0QUaRS4RmEnw7kvebaTqJBkfgLDui2cdPfY14TpvSqqTnOMuCRRkSTniYh/gCqsfI7iGR0ill41JchB+HR5wIDAQAB";
	private static final String FIXED_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCz4V0Yw5RGntUmDvY6M8Fci0Pr4v0gZDjpk4UsEpasAGNUKMoMafNu1Ij5/Ms7Z7fkKysBzGV3edODqkb42FR6kUXv33oO79GeSzE/pUX7z4AjCASWR9d3azAz9fu6faNqPcftuTd/tTDiI2y2ntN2F52ouAoMw/qQbzJmnYDO6URmGD4ex07wLn6E4GpjSgNMPatJYFMpSMv0sK40xL8Ky3y8dzfuRBZJJ7tfj3GCuoTRtv9H5eRtJzSeK8hEtCB0jRBRpFLhGYSfDuS95tpOokGR+AsO6LZx099jXhOm9KqpOc4y4JFGRJOeJiH+AKqx8juIZHSKWXjUlyEH4dHnAgMBAAECggEAGJCwXA9pknNifH4SlkQ3JeJG9lHba68ELrHcv/YR6Y9sATRI04Usn3ga9LjXMFerv1c92lMFkFlsz1BTvOweLVbljCqiyqNppwpxwVVax1bBEwenSLaq8D4NKStwozlBNeMWCJsAv0oJUPixggcSrc/v0fjANpoU/+CPq3/a9WafKI6/eJMDT/jqm/RnWE1EnBND7p7JNCZBiCww66JHGgDf8gcOB5bswJbB0Raql2hQtr77k1eqgT7l4t65MuL1WGzLisojUDingPw0GqTTBx//4mAZTDJeyLrhRiI6pIqFAiCFOK7ild6VaEW5lEXHCCafgKQN7lZWEaphwPL6IQKBgQDMegwb5kktwGa2vpqTJW9v5/rWaFJtR/Zc9wqegv2+aMJNhCTqpJGUZNe23cZNU9LnycUM+8AaNP+NYvimqw7vt7fHVqxemIU5Jn2y4JZja6JhSt9zPgP6ZAn8MOsrNx6Bud5HlZBNIQCeIEiIwP6Ws+qRLRtFA3WNreNV1uLuKQKBgQDhNLPc59FBFG3VtUDiQygQlCWKoMpmKwUKwJ32weAYXZ4JqqYqUO9sOXM+zhXsAQ5lZVCr7IULH3VsB2kEm4dSZI8mjcUm2A37OGO90zgpM5MKQbgYPJDmEb7YswGsdlMrYFqvtjIGVzsOLjOVomWUmFOIDbnseC437Cq+OX+hjwKBgBQeLfLQxYJq27A/MJYpqL0p5dQSj4mQ+vxKkhDmcnC5feSCi99KMciX42FqkIgrJyUHBQwx+MSbUklm43AChIWxXbOPI4ZrGzB0SQnAbwt5G5DW/QUppNqN3S2i4oBt++JnlycT4A62oIjuRDSwSFwrd3ixJ22y2W4HHcbkTY+BAoGAGMsDjitbRy3nlaJ5vS8lhwW4SFXBUpQj3vYrrdHUSPptfqTx3DDC2whMHrrzgOLw4crQoczNLNdOxxDdx+sTb8ewVs8+yDL6/xBLMsfBJBAQfQ2VJd3tGdcUSqkSYxk3uMvH0BZf1YDe+IWvmiahxkUq1QylzlnAxNlUL1MHMRMCgYBO84KlYTn51Gly4fT7+CXy3a3s10X1nS/hXa0uoU+RU6cWA/gfEnlTg82FcI7UuYqpCfdTG62f60huMP+Od9R5bR7j6ob0hXisItAJ3cyrLkQkLbAXYJpbhFSxkz1pPAbZDsJ32gA25vkt5B2McGJZCmCR8/nMs51jNIjaXcHDAQ==";

	// 公開鍵を取得
	public static PublicKey getPublicKey() throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(FIXED_PUBLIC_KEY);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}

	// 秘密鍵を取得
	public static PrivateKey getPrivateKey() throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(FIXED_PRIVATE_KEY);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}

	// 文字列を公開鍵で暗号化
	public static String encrypt(String plainText) throws Exception {
		PublicKey publicKey = getPublicKey();
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	// 文字列を秘密鍵で復号化
	public static String decrypt(String encryptedText) throws Exception {
		PrivateKey privateKey = getPrivateKey();
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
		byte[] decryptedBytes = cipher.doFinal(decodedBytes); // この行で BadPaddingException
		return new String(decryptedBytes);
	}

	// RSA鍵ペア(公開鍵、秘密鍵)を生成するメソッド
	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048); // 2048ビットの鍵を生成
		return keyGen.generateKeyPair();
	}

	public static List<String> Base64EncodedKeyPair(KeyPair keyPair) {
		List<String> KeyPair = new ArrayList<String>();
		KeyPair.add(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
		KeyPair.add(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
		return KeyPair;
	}

	public static void main(String[] args) {
		try {
			String message = "This is a secret message.";

			// 暗号化
			String encryptedMessage = encrypt(message);
			System.out.println("Encrypted: " + encryptedMessage);

			// 復号化
			String decryptedMessage = decrypt(encryptedMessage);
			System.out.println("Decrypted: " + decryptedMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
