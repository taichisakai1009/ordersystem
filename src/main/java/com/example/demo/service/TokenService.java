package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.TokensEntity;
import com.example.demo.Entity.TokensEntity.TokenStatus;
import com.example.demo.Repository.TokensRepository;
import com.example.demo.utils.HashUtil;
import com.example.demo.utils.RSAUtil;
import com.example.demo.utils.TokenUtil;

import jakarta.annotation.PostConstruct;

@Service
public class TokenService {

	private final TokensRepository tokensRepository;

	private final TokenUtil tokenUtil;

	private final HashUtil hashUtil;

	TokenService(TokensRepository tokensRepository, TokenUtil tokenUtil, HashUtil hashUtil) {
		this.tokensRepository = tokensRepository;
		this.tokenUtil = tokenUtil;
		this.hashUtil = hashUtil;
		System.out.println("TokenUtilのインスタンス:"+tokenUtil);
		System.out.println("TokenServiceのコンストラクタ呼び出し");
	}
	
    @PostConstruct
    public void init() {
        System.out.println("TokenService インスタンスが作成されました");
    }

	// トークンを作成してデータベースに保存
	public String createToken(ClerksEntity user, int validityMinutes) throws Exception {
		// シークレットキー生成
		String secretKey = tokenUtil.generateSecretKey();

		// シークレットキーのRSA化
		String encryptedKey = RSAUtil.encrypt(secretKey);

		// トークンのデータ（任意）
		String data = "user:" + user.getClerkId() + ",timestamp:" + System.currentTimeMillis();

		// トークン生成
		String token = tokenUtil.generateToken(data, secretKey);

		// トークンをハッシュ化
		String hashToken = hashUtil.hashStr(token);

		// トークンエンティティの作成
		TokensEntity tokensEntity = new TokensEntity();
		tokensEntity.setUser(user);
		tokensEntity.setToken(hashToken);
		tokensEntity.setDynamicKey(encryptedKey);
		tokensEntity.setCreatedAt(LocalDateTime.now());
		tokensEntity.setExpiresAt(LocalDateTime.now().plusMinutes(validityMinutes));
		tokensEntity.setStatus(TokenStatus.ACTIVE);

		tokensRepository.save(tokensEntity); // データベースに保存

		return token;
	}

	// トークンの検証
	public boolean validateToken(String token) {
		// トークンを検索
		Optional<TokensEntity> optionalToken = tokensRepository.findByToken(token);

		if (optionalToken.isPresent()) {
			TokensEntity tokensEntity = optionalToken.get();

			// ステータスがACTIVEか確認
			if (tokensEntity.getStatus() != TokenStatus.ACTIVE) {
				return false;
			}

			// 有効期限のチェック
			if (tokenUtil.isTokenExpired(tokensEntity.getExpiresAt())) {
				// 有効期限切れの場合はステータスを更新
				tokensEntity.setStatus(TokenStatus.EXPIRED);
				tokensRepository.save(tokensEntity);
				return false;
			}

			return true; // 有効
		}
		return false; // 無効
	}

	// トークンの失効
	public void revokeToken(String token) {
		Optional<TokensEntity> optionalToken = tokensRepository.findByToken(token);

		if (optionalToken.isPresent()) {
			TokensEntity tokensEntity = optionalToken.get();
			tokensEntity.setStatus(TokenStatus.REVOKED);
			tokensRepository.save(tokensEntity);
		}
	}

	// トークンから店員IDを取得,結果がない場合は0を返す
	public Integer getClerkIdByToken(String token) {
	    return Optional.ofNullable(tokensRepository.findClerkIdByToken(token)).orElse(0);
	}


	// トークンの定期的な削除
	public void clearToken(Integer day) {
		// day日前の日付を取得
		LocalDateTime severalDaysAgo = LocalDateTime.now().minus(day, ChronoUnit.DAYS);
		// day日前のデータを削除
		tokensRepository.deleteAllByCreatedAtBefore(severalDaysAgo);
	}
}
