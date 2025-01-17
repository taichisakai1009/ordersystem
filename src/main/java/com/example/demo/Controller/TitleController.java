package com.example.demo.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Python.PythonExecutor;
import com.example.demo.service.TitleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TitleController {

	private final TitleService titleService;

	TitleController(TitleService titleService) {
		this.titleService = titleService;
	}

	@Autowired
	PythonExecutor pythonExecutor;

	// タイトルの表示
	@RequestMapping("/")
	public String start() throws IOException, InterruptedException {
		System.out.println("タイトルの表示");
		pythonExecutor.pythonExecution("sympyTest", true, false);
		return "title/title";
	}

	// タイトルに戻る
	@RequestMapping(path = "/title/title", params = "title")
	public String back(Model model, HttpSession session) {

		titleService.bye(model, session);// 会計を終えた利用客の情報を削除
		titleService.addSeatNumber(model, session); // 座席番号をセッションから取得してモデル追加

		return "title/title";
	}

}