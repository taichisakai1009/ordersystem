package com.example.demo.Controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.OrderRecordDto;
import com.example.demo.Dto.OrderRecordDtoList;
import com.example.demo.Entity.PassengersEntity;
import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.Repository.PassengersRepository;
import com.example.demo.service.ChoiceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/clerks")
public class ClerksController {

	@Autowired
	ChoiceService choiceService;

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	PassengersRepository passengersRepository;

	// 利用客選択画面表示
	@RequestMapping(path = "/choice", params = "show")
	public String showChoiceView(Model model) {

		List<PassengersEntity> passengersList = passengersRepository.findAll();
		model.addAttribute("passengersList", passengersList);
		System.out.println("passengersList：" + passengersList);

		return "clerks/choicePassensior";
	}

	// 店員画面表示 どの注文履歴を取得するか？
	@RequestMapping(path = "/delivered", params = "show")
	public String show(Integer passengerId, Model model, HttpSession session) {
//		どの注文履歴を取得するか？
		OrderRecordDtoList orderRecordDtoList = choiceService.restOrderRecord(passengerId, session);
		model.addAttribute("orderRecordDtoList", orderRecordDtoList);
		System.out.println("店員画面ボタン");
		return "clerks/setDelivered";
	}

	@RequestMapping(path = "/delivered",params = "delivered")
	@ResponseBody // Ajaxレスポンスを返すために追加
	public ResponseEntity<?> delivered(@RequestParam Integer id, HttpSession session) {
		try {
			// 未配達フラグをオフにする
			orderDetailsRepository.updateUndeliveredFlgToFalse(id);

			// セッション情報の更新
			OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
			if (orderRecordDtoList != null) {
				List<OrderRecordDto> orderRecords = orderRecordDtoList.getOrderRecordDtoList();
				for (OrderRecordDto order : orderRecords) {
					if (order.getId() == id) {
						order.setUndeliveredFlg(false);
						break;
					}
				}
				session.setAttribute("orderRecord", orderRecordDtoList);
			}

			// JSONレスポンスを返す
			return ResponseEntity.ok(new HashMap<>() {
				{
					put("success", true);
					put("message", "配達済みに更新しました");
					put("updatedOrderId", id);
				}
			});
		} catch (Exception e) {
			// エラーハンドリング
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new HashMap<>() {
						{
							put("success", false);
							put("message", "更新中にエラーが発生しました");
						}
					});
		}
	}

	// ログイン成功時のメソッド
	@RequestMapping(path = "/login")
	public String test(Model model, HttpSession session) {
		System.out.println("ログイン成功");
		// ログインじょうたい
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean loginFlg = authentication != null
				&& authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName());
		session.setAttribute("loginFlg", loginFlg);
		System.out.println("authentication：" + authentication);
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
		model.addAttribute("orderRecordDtoList", orderRecordDtoList);
		return "clerks/setDelivered";
	}

	// メニューに戻る
	@RequestMapping(path = "/back")
	public String backToMenu() {
		System.out.println("メニューに戻る");
		return "order/choice";
	}
}
