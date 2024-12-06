package com.example.demo.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.OrderDetailsDto;
import com.example.demo.Dto.OrderDetailsDtoList;
import com.example.demo.Dto.OrderRecordDto;
import com.example.demo.Dto.OrderRecordDtoList;
import com.example.demo.Entity.DishesEntity;
import com.example.demo.Entity.OrdersEntity;
import com.example.demo.Entity.PassengersEntity;
import com.example.demo.Repository.DishesRepository;
import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.Repository.OrdersRepository;
import com.example.demo.Repository.PassengersRepository;
import com.example.demo.service.ChoiceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class ChoiceController {
	private final DishesRepository dishesRepository;
	private final PassengersRepository passengersRepository;
	private final OrdersRepository ordersRepository;
	private final OrderDetailsRepository orderDetailsRepository;
	private final ChoiceService choiceService;
	private final TitleController titleController;

	//	TitleController
	ChoiceController(DishesRepository dishesRepository, PassengersRepository passengersRepository,
			OrdersRepository ordersRepository, OrderDetailsRepository orderDetailsRepository,
			ChoiceService choiceService, TitleController titleController) {
		this.dishesRepository = dishesRepository;
		this.passengersRepository = passengersRepository;
		this.ordersRepository = ordersRepository;
		this.orderDetailsRepository = orderDetailsRepository;
		this.choiceService = choiceService;
		this.titleController = titleController;
	}

	// 座席番号をセッションから取得してモデル追加

	// 注文画面の初期表示を行う。
	@RequestMapping(path = "/choice", params = "show")
	public String showChoiceView(Integer seatNumber, Model model, HttpSession session) {
		// 座席番号をモデル追加
		session.setAttribute("seatNumber", seatNumber);
		choiceService.addSeatNumber(model, session);
		// 座席番号から注文履歴をセッションに設定
		List<PassengersEntity> passengers = choiceService.getPassengerBySeatNumberAndEatingFlg(seatNumber, true);
		if (passengers.size() == 1) {
			Integer passengerId = passengers.get(0).getPassengerId();
			session.setAttribute("passengerId", passengerId);
			choiceService.addPassengerId(model, session);
			choiceService.restOrderRecord(passengerId, session);
		}
		return "order/choice";
	}

	// 入力した注文番号に応じて料理の検索をする。
	//	@RequestMapping(path = "/choice", params = "search")
	//	public ResponseEntity<String> findDishByOrderNumber(@RequestParam int orderNumber) {
	//		String dishName = choiceService.getDishName(orderNumber);
	//		return ResponseEntity.ok(dishName);// これに.getBody()を加えるとdishNameが取得できる。
	//	}

	// 入力した注文番号に応じて料理の検索をする。
	@ResponseBody
	@RequestMapping(path = "/choice", params = "search")
	public ResponseEntity<Map<String, Object>> findDishByOrderNumber(@RequestParam int orderNumber) {
		Map<String, Object> response = new HashMap<>();
		DishesEntity dishesEntity = choiceService.getDishByOrderNumber(orderNumber);
		response.put("dishesEntity", dishesEntity);
		System.out.println("商品名："+dishesEntity.getDishName());
		return ResponseEntity.ok(response);
	}

	// モデルCartに入れるとき呼び出す。
	@RequestMapping(path = "/choice", params = "insert")
	public void addToCart(Integer orderNumber, Integer quantity, Model model, HttpSession session) {
		choiceService.addSeatNumber(model, session);
		//		choiceService.addPassengerId(model, session);
		System.out.println("Cartに追加。orderNumber：" + orderNumber + "、quantity：" + quantity);
		choiceService.addToCart(orderNumber, quantity, session);
	}

	// カートの中身を表示する。
	@RequestMapping(path = "/choice", params = "viewCart")
	public ResponseEntity<Map<String, Object>> viewCart(Model model, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		List<OrderDetailsDto> orderDetailsDtoList = choiceService.viewCart(model, session);
		response.put("orderDetailList", orderDetailsDtoList);
		return ResponseEntity.ok(response);
	}

	// 注文履歴を表示する。
	@RequestMapping(path = "/choice", params = "viewRecord")
	public ResponseEntity<Map<String, Object>> viewRecord(Model model, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");
		response.put("orderRecordList", orderRecordDtoList);
		return ResponseEntity.ok(response);
	}

	// 注文の一部を取り消す。
	@RequestMapping(path = "/choice", params = "remove")
	public ResponseEntity<String> removeFromCart(String dishName, Model model,
			HttpSession session) {
		choiceService.removeFromCart(dishName, model, session);
		return ResponseEntity.ok(dishName);
	}

	// 注文の数量を確認画面で訂正する。
	@RequestMapping(path = "/choice", params = "amendment")
	public void amendment(HttpSession session, Integer orderNumber, Integer quantity) {
		choiceService.amendment(session, orderNumber, quantity);
	}

	// 注文内容を送信する。
	@RequestMapping(path = "/choice", params = "order")
	@ResponseBody
	public ResponseEntity<OrderDetailsDtoList> orderDishes(@RequestBody OrderDetailsDtoList orderDetails,
			Model model, HttpSession session) {
		// 注文詳細からリストと座席番号を分離
		List<OrderDetailsDto> orderDetailsDtoList = orderDetails.getOrderDetailsDtoList();
		Integer seatNumber = orderDetails.getSeatNumber();

		System.out.println("注文内容：" + orderDetails);

		// 注文テーブルに登録（初回の場合利用者テーブルも登録）
		OrdersEntity ordersEntity = choiceService.insertOrders(seatNumber, model, session);

		// 注文テーブルからIDと注文時間を取得
		Integer orderId = ordersEntity.getOrderId();
		LocalTime orderTime = ordersEntity.getOrderTime();

		// 注文時間のフォーマットを"HH:mm:ss"に設定 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // フォーマットされた時刻を取得
		String formattedTime = orderTime.format(formatter);

		// 今までの注文履歴を取得
		OrderRecordDtoList orderRecordDtoList = choiceService.getOrderRecords(session);

		// 注文履歴から今までの注文リストと合計金額を取得
		List<OrderRecordDto> OrderRecordList = orderRecordDtoList.getOrderRecordDtoList();
		Integer totalPrice = orderRecordDtoList.getTotalPrice();

		// 注文内容ごとに処理
		for (OrderDetailsDto order : orderDetailsDtoList) {

			// 注文詳細テーブルに挿入、外部キー制約により、注文テーブルのIDをセット
			// 識別idを取得
			Integer id = choiceService.insertOrderDetails(order, orderId);

			// 注文リストに追加
			OrderRecordDto orderRecordDto = new OrderRecordDto();
			orderRecordDto.setId(id);
			orderRecordDto.setOrderId(orderId);
			orderRecordDto.setDishName(order.getDishName());
			orderRecordDto.setPrice(order.getPrice());
			orderRecordDto.setQuantity(order.getQuantity());
			orderRecordDto.setOrderTime(formattedTime);
			orderRecordDto.setUndeliveredFlg(true);
			OrderRecordList.add(orderRecordDto);

			totalPrice += order.getPrice() * order.getQuantity(); // 合計金額の計算
		}
		orderRecordDtoList.setTotalPrice(totalPrice);
		orderRecordDtoList.setOrderRecordDtoList(OrderRecordList);
		// 注文履歴に追加
		session.setAttribute("orderRecord", orderRecordDtoList);
		System.out.println("注文情報：" + orderRecordDtoList);

		// 注文リストを削除
		session.removeAttribute("Cart");
		return ResponseEntity.ok(orderDetails);
	}

	// お会計
	@RequestMapping(path = "/choice", params = "title")
	public void restaurantBill(@RequestParam Integer passengerId, Model model, HttpSession session) {
		choiceService.restaurantBill(passengerId);
		session.removeAttribute("passengerId");
		session.removeAttribute("orderRecord");
		System.out.println("お会計passengerId：" + passengerId);
	}

	// fetchだとモデル追加をビューに反映できないのでwindow.location.hrefで呼び出す。
	@RequestMapping(path = "/choice", params = "model")
	public void addModel(Model model, HttpSession session) {
		choiceService.addPassengerId(model, session);
		choiceService.addSeatNumber(model, session);
	}

}