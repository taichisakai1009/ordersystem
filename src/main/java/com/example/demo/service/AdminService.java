package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.RolesEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.Repository.RolesRepository;

import jakarta.mail.MessagingException;

@Service
public class AdminService {

	private final ClerksRepository clerksRepository;

	private final RolesRepository rolesRepository;

	private final PasswordEncoder passwordEncoder;

	private final EmailService emailService;
	
	private final TemplateEngine templateEngine;

	AdminService(ClerksRepository clerksRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder,
			EmailService emailService, TemplateEngine templateEngine) {
		this.clerksRepository = clerksRepository;
		this.rolesRepository = rolesRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.templateEngine = templateEngine;
	}

	public void updateClerkDetails(Integer clerkId, String name, String mailAddress, String tel) {
		clerksRepository.updateClerkDetails(clerkId, name, mailAddress, tel);
		
	}

	public void deleteByClerkId(Integer clerkId) {
		clerksRepository.deleteByClerkId(clerkId);
	}

	// 入力した店員番号がすでに使われていないか調べる
	public boolean existsByClerkNumber(Integer clerkNumber) {
		return clerksRepository.existsByClerkNumber(clerkNumber);
	}

	// 氏名、メールアドレス、電話番号のバリデーションチェック
	public boolean registValidationCheck(String name, String mailAddress, String tel, Model model) {

		String mailAddressRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // メールアドレスのバリデーション（user@example.com のような形式かどうか）
		String telRegex = "^(0\\d{9,10})$"; // 固定電話・携帯電話のバリデーション（ハイフンなし）

		boolean nameCheckflg = name != null;
		boolean mailAddressCheckflg = mailAddress != null && mailAddress.matches(mailAddressRegex);
		boolean telCheckflg = tel != null && tel.matches(telRegex);

		String nameError = "・名前を入力してください";
		String mailError = "・入力されたメールアドレスが正しくありません。半角英数字および記号（+、_、.、-） を含む user@example.com の形式で入力してください。";
		String telError = "・入力された電話番号が正しくありません。0から始まるハイフンなしの10桁または11桁の数字 を入力してください（例: 09012345678）";

		boolean errorFlg = true;

		if (!nameCheckflg) {
			model.addAttribute("nameError", nameError);
			errorFlg = false;
		}
		if (!mailAddressCheckflg) {
			model.addAttribute("mailError", mailError);
			errorFlg = false;
		}
		if (!telCheckflg) {
			model.addAttribute("telError", telError);
			errorFlg = false;
		}

		return errorFlg;

	}
	
	// 電話番号にハイフンをつける
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            // 10桁の電話番号の場合
            return phoneNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else if (phoneNumber.length() == 11) {
            // 11桁の電話番号の場合
            return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else {
            // 異なる桁数の場合（エラー処理）
            throw new IllegalArgumentException("電話番号は10桁または11桁である必要があります");
        }
    }
    
    // 登録するメールアドレスに重複がないか判定する
    public boolean existsByMailAddress(String mailAddress) {
    	return clerksRepository.existsByMailAddress(mailAddress);
    }

	// 店員情報の登録を行う
	public ClerksEntity registClerk(String name, Integer clerkNumber, String rawPassword, String mailAddress,
			String tel,
			String startDateStr, Integer roleId) throws MessagingException {
		ClerksEntity clerksEntity = new ClerksEntity();
		String hashedPassword = passwordEncoder.encode(rawPassword);
		LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
		clerksEntity.setName(name);
		clerksEntity.setClerkNumber(clerkNumber);
		clerksEntity.setPassword(hashedPassword);
		clerksEntity.setMailAddress(mailAddress);
		clerksEntity.setTel(formatPhoneNumber(tel));
		clerksEntity.setStartDate(startDate);
		// ロールをデータベースから取得
		RolesEntity role = rolesRepository.findById(roleId)
				.orElseThrow(() -> new RuntimeException("権限が見つかりませんでした。"));
		clerksEntity.setRole(role);
		clerksRepository.save(clerksEntity);
		
        // メール本文をテンプレートから生成
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("clerkNumber", clerkNumber);
        context.setVariable("rawPassword", rawPassword);
        String text = templateEngine.process("admin/passwordNotification", context);
		// パスワード通知メールを送信
		String subject = "アカウントの初期パスワード通知";
//		String text = "こんにちは " + name + " 様\n\n以下の店員番号と初期パスワードを使用してログインしてください。" + "\n\n店員番号：" + clerkNumber + "\n\nパスワード: " + rawPassword
//				+ "\n\nセキュリティのため、最初にログインした際に必ずパスワードを変更してください。";
		emailService.sendSimpleMessage(mailAddress, subject, text);
		return clerksEntity;
	}

}
