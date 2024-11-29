package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.utils.PasswordUtils;

@Controller
public class TitleController {

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	// タイトルの表示
	@RequestMapping("/")
	public String start() {
		String password = "password";
		System.out.println("password："+PasswordUtils.password(password));
		return "title/title";
	}

	// タイトルに戻る
	@RequestMapping(path = "/title/title", params = "title")
	public String back() {
		return "title/title";
	}

}