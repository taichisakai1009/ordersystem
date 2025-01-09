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

	// 会計を終えた利用客の情報を削除
	public void bye(Model model, HttpSession session) {
		model.asMap().remove("passengerId"); // モデルから利用客IDを除去
		session.removeAttribute("orderRecord"); // 注文履歴をリセット
	}
}
