package com.example.demo.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.Entity.ClerksEntity;

/**
 * Spring SecurityでClerks（店員）エンティティを表現するためのカスタムUserDetails実装クラス。
 * このクラスは、店員の情報をSpring Securityに提供し、認証および認可プロセスで使用されます。
 */
public class ClerkDetails implements UserDetails {
    // 店員の情報を保持するClerksEntity
    private final ClerksEntity clerk;

    // ClerkDetailsをClerksEntityで初期化するコンストラクタ
    public ClerkDetails(ClerksEntity clerk) {
        this.clerk = clerk;
    }

    /**
     * 店員の権限（役割）を返します。
     * この実装では、店員の役割に基づいて権限を付与します。
     * @return 店員の役割に基づくSimpleGrantedAuthorityのリスト
     */
    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        // 店員の役割に「ROLE_」を付けてSpring Securityに渡す
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + clerk.getRole().getName())
        );
    }

    /**
     * 店員のパスワードを返します。
     * @return 店員のパスワード
     */
    @Override
    public String getPassword() {
        return clerk.getPassword();
    }

    /**
     * 店員のユーザー名（クラーク番号）を返します。
     * @return 店員のクラーク番号を文字列として返す
     */
    @Override
    public String getUsername() {
        return String.valueOf(clerk.getClerkNumber());
    }

    /**
     * アカウントの有効期限が切れているかどうかを判定します。
     * @return 常にtrueを返し、アカウントは期限切れでないとしています。
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントがロックされているかどうかを判定します。
     * @return 常にtrueを返し、アカウントはロックされていないとしています。
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 資格情報（パスワード）が期限切れでないかどうかを判定します。
     * @return 常にtrueを返し、資格情報は期限切れでないとしています。
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * アカウントが有効かどうかを判定します。
     * @return 常にtrueを返し、アカウントは有効であるとしています。
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
