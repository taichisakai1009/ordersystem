//package com.example.demo.security;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.Entity.ClerksEntity;
//import com.example.demo.Repository.ClerksRepository;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//	@Autowired
//	private ClerksRepository clerkRepository;
//
//	// 店員番号（ユーザー名）を元に店員を検索。UserDetails型のデータを返す。
//	@Override
//	public UserDetails loadUserByUsername(String clerkNumber) throws UsernameNotFoundException {
//		ClerksEntity clerksEntity = clerkRepository.findByClerkNumber(Integer.parseInt(clerkNumber))
//				.orElseThrow(() -> new UsernameNotFoundException("Clerk not found"));
//		String clerkNumberStr = String.valueOf(clerksEntity.getClerkNumber()); // ユーザー名
//		String password = clerksEntity.getPassword();
//		List<SimpleGrantedAuthority> singleRole = List.of(new SimpleGrantedAuthority(clerksEntity.getRole().getName())); // 単一権限をリストに
//		System.out.println("ユーザー名(ID):"+ clerkNumberStr);
//		System.out.println("パスワード：" + password);
//		System.out.println("単一権限："+ singleRole); // "単一権限：[Manager]"のように表示される。
//
//		return new User(clerkNumberStr, password, singleRole);
//
//	}
//
//}