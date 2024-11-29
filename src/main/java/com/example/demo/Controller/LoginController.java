package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.ChoiceService;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private ChoiceService choiceService;

	// ログイン判定(SplingSecurityの実装をしたら必要ない)
	//	@RequestMapping(path = "/login", params = "login")
	//	public String doLogin(Integer clerkNumber, String password) throws NoSuchAlgorithmException {
	//		boolean loginFlg = loginService.doLogin(clerkNumber, password);
	//		if (loginFlg) {
	//			return "test/test";
	//		} else {
	//			return "login/login";
	//		}
	//	}

	// ログイン画面の表示
	@RequestMapping(path = "/login", params = "show")
	public String showLoginView() {
		return "login/login";
	}

	// ログアウト成功
	@RequestMapping(path = "/login", params = "logout")
	public String logout(HttpSession session) {
		choiceService.restOrderRecord(1, session);
		System.out.println("ログアウト");
		return "order/choice";
	}

	// 注文画面に戻る
	@RequestMapping(path = "/login", params = "back")
	public String back() {
		System.out.println("注文画面に戻る");
		return "order/choice";
	}
}