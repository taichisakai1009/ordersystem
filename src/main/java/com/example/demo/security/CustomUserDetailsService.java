package com.example.demo.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private ClerksRepository clerkRepository;

	// 店員番号（ユーザー名）を元に店員を検索。UserDetails型のデータを返す。
	@Override
	public UserDetails loadUserByUsername(String clerkNumber) throws UsernameNotFoundException {
		// ここでユーザー情報を取得し、UserDetailsを返す
		// clerkNumberを数値に変換（例: 数値チェックや変換処理が必要な場合）
		try {
			int clerkNum = Integer.parseInt(clerkNumber);
			ClerksEntity clerksEntity = clerkRepository.findByClerkNumber(clerkNum)
					.orElseThrow(() -> new UsernameNotFoundException("Clerk not found"));

			return new org.springframework.security.core.userdetails.User(
					String.valueOf(clerksEntity.getClerkNumber()), // ユーザー名
					clerksEntity.getPassword(), // パスワード
					Collections.singletonList(new SimpleGrantedAuthority(clerksEntity.getRole()))); // 権限
		} catch (NumberFormatException e) {
			throw new UsernameNotFoundException("Invalid clerk number format");
		}
	}
}