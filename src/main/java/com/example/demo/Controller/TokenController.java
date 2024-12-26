package com.example.demo.Controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.example.demo.service.TokenService;

@Controller
@EnableScheduling
public class TokenController {
	
	private final TokenService tokenService;

	public TokenController (TokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	// 24時間以上前のトークンを削除
	@Scheduled(cron = "0 0 0 * * ?")
	public void clearToken () {
		tokenService.clearToken(1); // 一日前のトークンを削除
	}
}
