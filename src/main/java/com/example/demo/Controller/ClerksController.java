package com.example.demo.Controller;

import java.time.LocalDate;
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
import com.example.demo.Dto.SeeClerksDto;
import com.example.demo.Dto.SeeOrdersDto;
import com.example.demo.Dto.SeePassengersDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.DishesEntity;
import com.example.demo.Entity.OrderDetailsEntity;
import com.example.demo.Entity.OrdersEntity;
import com.example.demo.Entity.PassengersEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.Repository.DishesRepository;
import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.Repository.OrdersRepository;
import com.example.demo.Repository.PassengersRepository;
import com.example.demo.service.ChoiceService;
import com.example.demo.service.ClerksService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/clerks")
public class ClerksController {

	@Autowired
	ChoiceService choiceService;
	
	@Autowired
	ClerksService clerksService;

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	PassengersRepository passengersRepository;
	
	@Autowired
	DishesRepository dishesRepository;
	
	@Autowired
	ClerksRepository clerksRepository;
	
	@Autowired
	HttpSession session;
	
    // ホーム画面表示
	@RequestMapping(path = "/home")
	public String showAdminPage(Model model) {
		ClerksEntity clerk = (ClerksEntity) session.getAttribute("clerk");
		model.addAttribute("clerk", clerk);
		return "clerks/home";
	}

	// 利用客選択画面表示
//	@PreAuthorize("hasRole('Regular', 'Manager')")
	@RequestMapping(path = "/choice", params = "show")
	public String showChoiceView(Model model) {

		List<PassengersEntity> passengers = passengersRepository.findAll();
		List<SeePassengersDto> passengersList = clerksService.setSeePassengersDtoList(passengers);
		
		Integer passengerCount = passengersList.size();
		
		model.addAttribute("passengersList", passengersList);
		model.addAttribute("passengerCount", passengerCount);

		System.out.println("passengersList：" + passengersList);

		return "clerks/choicePassensior";
	}

	// 注文一覧画面表示
	@RequestMapping(path = "/delivered", params = "show")
	public String showOrders(Integer passengerId, Model model, HttpSession session) {
		List<OrdersEntity> orders = ordersRepository.findByPassengerId(passengerId);
		List<SeeOrdersDto> ordersList = clerksService.setSeeOrdersDtoList(orders);
		System.out.println("注文データ："+ordersList);
		model.addAttribute("orders", ordersList);
		model.addAttribute("passengerId", passengerId);
		return "clerks/choiceOrder";
	}

	// 注文詳細画面表示
	@RequestMapping(path = "/delivered", params = "setPage")
	public String show(Integer passengerId, Integer orderId, Model model, HttpSession session) {
		//		どの注文履歴を取得するか？
		List<OrderDetailsEntity> orderDetails = orderDetailsRepository.findByOrderId(orderId);
		PassengersEntity passenger = passengersRepository.findByPassengerId(passengerId);
		boolean eatingFlg = passenger.isEatingFlg();
		System.out.println("注文詳細データ："+orderDetails);
		model.addAttribute("orderDetails", orderDetails);
		model.addAttribute("passengerId", passengerId);
		model.addAttribute("eatingFlg", eatingFlg);
		return "clerks/setDelivered";
	}
	
	// お届け済にする。
	@RequestMapping(path = "/delivered", params = "delivered")
	@ResponseBody // Ajaxレスポンスを返すために追加
	public ResponseEntity<?> delivered(@RequestParam Integer id, HttpSession session) {
		try {
			// 未配達フラグをオフにする
			orderDetailsRepository.updateUndeliveredFlgToFalse(id);

			// セッション情報の更新 セッションではなく直接取っている
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
	
	// 同じ注文IDの商品がすべて配達済みになったらフラグオフ。
	@RequestMapping(path = "/delivered", params = "orders")
	@ResponseBody // Ajaxレスポンスを返すために追加
	public ResponseEntity<Integer> ordersDelivered(Integer orderId) {
		List<OrderDetailsEntity> sameOrder = orderDetailsRepository.findByOrderId(orderId);
		int listSize = sameOrder.size();
		if (listSize >= 1) {
			boolean allFlagsFalse = sameOrder.stream()
					.noneMatch(OrderDetailsEntity::isUndeliveredFlg); // すべてのフラグがfalseならtrueを返す。strermAPI

			if (allFlagsFalse) {
				ordersRepository.updateUndeliveredFlgByOrderId(orderId);
				System.out.println("注文テーブルのフラグ更新");
			} else {
				System.out.println("未配達フラグがオンの商品データが存在します");
			}
		} else {
			throw new RuntimeException("注文が見つかりませんでした: orderId=" + orderId);
		}
		return ResponseEntity.ok(orderId);
	}

	// 同じ注文IDの商品がすべて配達済みになったらフラグオフ。
	@RequestMapping(path = "/delivered", params = "passenger")
	@ResponseBody // Ajaxレスポンスを返すために追加
	public ResponseEntity<Integer> passengersDelivered(Integer passengerId) {
		List<OrdersEntity> samePassenger = ordersRepository.findByPassengerId(passengerId);
		int listSize = samePassenger.size();
		if (listSize >= 1) {
			boolean allFlagsFalse = samePassenger.stream()
					.noneMatch(OrdersEntity::isUndeliveredFlg); // すべてのフラグがfalseならtrueを返す。strermAPI

			if (allFlagsFalse) {
				passengersRepository.updateUndeliveredFlgByPassengerId(passengerId);
				System.out.println("利用客テーブルのフラグ更新");
			} else {
				System.out.println("未配達フラグがオンの注文データが存在します");
			}
		} else {
			throw new RuntimeException("利用客が見つかりませんでした: passengerId=" + passengerId);
		}
		return ResponseEntity.ok(passengerId);
	}
	
	// 商品一覧画面
	@RequestMapping(path = "/dishes")
	public String dishes(Model model) {
		List<DishesEntity> dishes = dishesRepository.findAll();
		Integer dishesCount = dishes.size();
		model.addAttribute("dishes", dishes);
		model.addAttribute("dishesCount", dishesCount);
		return "clerks/dishes";
	}
	
	//	トグルボタンで提供フラグを操作
	@RequestMapping(path="/dishes", params="onSale")
	@ResponseBody
	public ResponseEntity<Void> changeSaleStatus(@RequestParam("dishId") Integer dishId) {
	    DishesEntity dish = dishesRepository.findById(dishId).orElseThrow();
	    dish.setOnSaleFlg(dish.isOnSaleFlg() == true ? false : true); // 状態を切り替える
	    dishesRepository.save(dish);
	    System.out.println(dish.getDishName()+"、提供フラグ："+dish.isOnSaleFlg());
	    return ResponseEntity.ok().build();
	}
	
	// 店員一覧画面表示
	@RequestMapping(path="/clerks")
	public String showClerks(Model model) {
		List<ClerksEntity> clerksEntity = clerksRepository.findAll();
		List<SeeClerksDto> clerks = clerksService.setSeeClerksDtoList(clerksEntity);
		model.addAttribute("clerks", clerks);
		return "clerks/clerks";
	}
	
	// 店員選択画面表示
	@RequestMapping(path = "/clerkSelect", params = "show")
	public String showClerkSelect() {
		System.out.println("店員選択画面表示");
		return "clerks/clerkSelect";
	}

	// 店員番号で従業員を検索
	@RequestMapping(path = "/clerkSelect", params = "numberSearch")
	@ResponseBody // JSONレスポンスを返すために追加
	public List<ClerksEntity> searchClerksByNumber(@RequestParam Integer clerkNumber) {
		return clerksService.findClerksByNumber(clerkNumber);
	}

	// 氏名（あいまい検索）で従業員を検索
	@RequestMapping(path = "/clerkSelect", params = "nameSearch")
	@ResponseBody // JSONレスポンスを返すために追加
	public List<ClerksEntity> searchClerksByName(@RequestParam String name) {
		return clerksService.findByNameContaining(name);
	}

	// 店員管理画面表示
	@RequestMapping(path = "/clerkManagement", params = "show")
	public String showClerkManagement(Integer clerkId, String name, Integer clerkNumber, String mailAddress, String tel,
			LocalDate startDate, String roleName, Model model) {
		model.addAttribute("clerkId", clerkId);
		model.addAttribute("name", name);
		model.addAttribute("clerkNumber", clerkNumber);
		model.addAttribute("mailAddress", mailAddress);
		model.addAttribute("tel", tel);
		model.addAttribute("startDate", startDate);
		model.addAttribute("roleName", roleName);
		// 自分の情報によってボタンの活性、非活性を切り替え
		ClerksEntity clerk = (ClerksEntity) session.getAttribute("clerk");
		model.addAttribute("clerk", clerk);
		return "clerks/clerkManagement";
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
