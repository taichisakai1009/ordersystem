package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

@Service
public class TitleService {

		// 座席番号をセッションから取得してモデル追加
	public Integer addSeatNumber(Model model, HttpSession session) {
		Integer seatNumber = (Integer) session.getAttribute("seatNumber");
		if (seatNumber != null) {
			model.addAttribute("seatNumber", seatNumber);
		} else {
			model.addAttribute("seatNumber", "未設定");
		}
		return seatNumber;
	}
}
