package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Dto.OrderRecordDtoList;
import com.example.demo.Repository.OrderDetailsRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class TitleController {
	
	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	// タイトルの表示
	@RequestMapping("/")
	public String start() {
		return "title/title";
	}
	
	// タイトルに戻る
	@RequestMapping(path="/title/title", params="title")
	public String back() {
		return "title/title";
	}
	
	// テスト画面
	@RequestMapping(path="/title/title", params="test")
	public String test(Model model,HttpSession session) {
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
		model.addAttribute("orderRecordDtoList",orderRecordDtoList);
		return "test/test";
	}
	
	// テスト画面
	@RequestMapping(path="/title/delivered", params="delivered")
	public String delivered(Integer id, Model model,HttpSession session) {
		System.out.println("id："+id);
		System.out.println(orderDetailsRepository.updateUndeliveredFlgToFalse(id));
		return "title/title";
	}
	
	// メニューに戻る
	@RequestMapping(path="/title/title", params="menu")
	public String backToMenu() {
		return "order/choice";
	}
}
