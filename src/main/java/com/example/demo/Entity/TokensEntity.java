package com.example.demo.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tokens") // テーブル名を指定
public class TokensEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成ID
    @Column(name = "id") // カラム名を指定
    private Long id;

    @Column(name = "token", nullable = false, length = 512) // トークンの値
    private String token;

    @Column(name = "dynamic_key", nullable = false, length = 512) // シークレットキー
    private String dynamicKey;

    @ManyToOne(fetch = FetchType.LAZY) // 遅延ロードでユーザーを取得
    @JoinColumn(name = "user_id", nullable = false) // 外部キー
    private ClerksEntity user;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") // 作成日時
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false) // 有効期限
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING) // ステータス（列挙型）
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVE', 'REVOKED', 'EXPIRED') DEFAULT 'ACTIVE'")
    private TokenStatus status;

    public enum TokenStatus { // ステータスの列挙型定義
        ACTIVE,
        REVOKED,
        EXPIRED
    }
}

