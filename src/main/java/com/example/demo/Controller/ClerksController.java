package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Dto.OrderRecordDto;
import com.example.demo.Dto.OrderRecordDtoList;
import com.example.demo.Repository.OrderDetailsRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/clerks")
public class ClerksController {

	@Autowired
	OrderDetailsRepository orderDetailsRepository;
	
	// 店員画面表示
	@RequestMapping(path = "/delivered", params="show")
	public String show(Model model, HttpSession session) {
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
		model.addAttribute("orderRecordDtoList", orderRecordDtoList);
		System.out.println("店員画面ボタン");
		return "clerks/clerks";
	}
	// 未配達フラグオフ
	@RequestMapping(path = "/delivered", params="delivered")
	public String delivered(Integer id, Model model, HttpSession session) {
		System.out.println("id：" + id);
		System.out.println(orderDetailsRepository.updateUndeliveredFlgToFalse(id));
		// セッション情報も更新したい
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
			model.addAttribute("orderRecordDtoList", orderRecordDtoList);
		}
		return "clerks/clerks";
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
		System.out.println("authentication："+authentication);
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
		model.addAttribute("orderRecordDtoList", orderRecordDtoList);
		return "clerks/clerks";
	}

	// メニューに戻る
	@RequestMapping(path = "/back")
	public String backToMenu() {
		System.out.println("メニューに戻る");
		return "order/choice";
	}
}
