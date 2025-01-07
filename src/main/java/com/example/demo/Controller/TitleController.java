package com.example.demo.Controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.TitleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TitleController {

	private final TitleService titleService;
	
	TitleController(TitleService titleService) {
		this.titleService = titleService;
	}

	// タイトルの表示
	@RequestMapping("/")
	public String start() throws IOException {
		return "title/title";
	}

	// タイトルに戻る
	@RequestMapping(path = "/title/title", params = "title")
	public String back(Model model, HttpSession session) {
		
		model.asMap().remove("passengerId"); // モデルから利用客IDを除去
		session.removeAttribute("orderRecord"); // 注文履歴をリセット
		titleService.addSeatNumber(model, session); // 座席番号をセッションから取得してモデル追加

		return "title/title";
	}

}