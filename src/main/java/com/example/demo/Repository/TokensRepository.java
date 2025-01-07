package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.Entity.TokensEntity;

import jakarta.transaction.Transactional;

public interface TokensRepository extends JpaRepository<TokensEntity, Long> {
	
	
    // トークンで検索
    Optional<TokensEntity> findByToken(String token);

    // 有効なトークンを検索
    Optional<TokensEntity> findByTokenAndStatus(String token, TokensEntity.TokenStatus status);
    
    // 前のトークンを削除
    @Transactional
    @Modifying
    // expiresAtが指定された日数より前のトークンを削除するメソッド
    void deleteAllByCreatedAtBefore(LocalDateTime expiresAt);
    
    @Query("SELECT t.user.clerkId FROM TokensEntity t WHERE t.token = :token")
    Integer findClerkIdByToken(String token);
}

