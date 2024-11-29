package com.example.demo.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "clerks")
public class ClerksEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 店員ID
	private Integer clerkId;
	
	// 権限
	private String role;
	
	// 氏名
	private String name;
	
	// 店員番号
	private Integer clerkNumber;
	
	// パスワード
	private String password;
	
	// メールアドレス
	private String mailAddress;
	
	// 電話番号
	private String tel;
	
	// 利用開始日
	private LocalDate startDate;
}
