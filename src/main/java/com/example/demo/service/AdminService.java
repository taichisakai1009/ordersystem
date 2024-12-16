package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.RolesEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.Repository.RolesRepository;

@Service
public class AdminService {

	private final ClerksRepository clerksRepository;

	private final RolesRepository rolesRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailService emailService;

	AdminService(ClerksRepository clerksRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder,
			EmailService emailService) {
		this.clerksRepository = clerksRepository;
		this.rolesRepository = rolesRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

	public void updateClerkDetails(Integer clerkId, String name, String mailAddress, String tel) {
		clerksRepository.updateClerkDetails(clerkId, name, mailAddress, tel);
	}

	public void deleteByClerkId(Integer clerkId) {
		clerksRepository.deleteByClerkId(clerkId);
	}

	// 登録する店員番号がすでに使われていないか調べる
	public boolean existsByClerkNumber(Integer clerkNumber) {
		System.out.println("店員番号："+clerkNumber+"、重複フラグ："+clerksRepository.existsByClerkNumber(clerkNumber));
		return clerksRepository.existsByClerkNumber(clerkNumber);
	}

	// 店員情報の登録を行う
	public ClerksEntity registClerk(String name, Integer clerkNumber, String rawPassword, String mailAddress, String tel,
			String startDateStr, Integer roleId) {
		ClerksEntity clerksEntity = new ClerksEntity();
		String hashedPassword = passwordEncoder.encode(rawPassword);
		LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
		clerksEntity.setName(name);
		clerksEntity.setClerkNumber(clerkNumber);
		clerksEntity.setPassword(hashedPassword);
		clerksEntity.setMailAddress(mailAddress);
		clerksEntity.setTel(tel);
		clerksEntity.setStartDate(startDate);
		// ロールをデータベースから取得
		RolesEntity role = rolesRepository.findById(roleId)
				.orElseThrow(() -> new RuntimeException("権限が見つかりませんでした。"));
		clerksEntity.setRole(role);
		clerksRepository.save(clerksEntity);
		// パスワード通知メールを送信
        String subject = "アカウントの初期パスワード通知";
        String text = "こんにちは " + name + " 様\n\n以下の初期パスワードを使用してログインしてください。\n\nパスワード: " + rawPassword + "\n\nセキュリティのため、最初にログインした際に必ずパスワードを変更してください。";
        emailService.sendSimpleMessage(mailAddress, subject, text);
		return clerksEntity;
	}

}
