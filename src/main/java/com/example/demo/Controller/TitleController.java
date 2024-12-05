package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.service.ChoiceService;
import com.example.demo.utils.PasswordUtils;

import jakarta.servlet.http.HttpSession;

@Controller
public class TitleController {

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	ChoiceService choiceService;

	// タイトルの表示
	@RequestMapping("/")
	public String start() {
		String password = "password";
		System.out.println("password："+PasswordUtils.password(password));
		return "title/title";
	}

	// タイトルに戻る
	@RequestMapping(path = "/title/title", params = "title")
	public String back(Model model, HttpSession session) {
		// 利用客番号をリセット
		model.asMap().remove("passengerId");
		session.removeAttribute("orderRecord");
		choiceService.addSeatNumber(model, session);

		return "title/title";
	}

}