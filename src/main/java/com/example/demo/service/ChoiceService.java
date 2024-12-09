package com.example.demo.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.Dto.OrderDetailsDto;
import com.example.demo.Dto.OrderDetailsDtoList;
import com.example.demo.Dto.OrderRecordDto;
import com.example.demo.Dto.OrderRecordDtoList;
import com.example.demo.Entity.DishesEntity;
import com.example.demo.Entity.OrderDetailsEntity;
import com.example.demo.Entity.OrdersEntity;
import com.example.demo.Entity.PassengersEntity;
import com.example.demo.Repository.DishesRepository;
import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.Repository.OrdersRepository;
import com.example.demo.Repository.PassengersRepository;
import com.example.demo.exception.MultiplePassengersException;

import jakarta.servlet.http.HttpSession;

@Service
public class ChoiceService {

	private final DishesRepository dishesRepository;
	private final PassengersRepository passengersRepository;
	private final OrdersRepository ordersRepository;
	private final OrderDetailsRepository orderDetailsRepository;

	ChoiceService(DishesRepository dishesRepository, PassengersRepository passengersRepository,
			OrdersRepository ordersRepository, OrderDetailsRepository orderDetailsRepository) {
		this.dishesRepository = dishesRepository;
		this.passengersRepository = passengersRepository;
		this.ordersRepository = ordersRepository;
		this.orderDetailsRepository = orderDetailsRepository;
	}

	// 注文番号に応じて商品エンティティを検索
	public DishesEntity getDishByOrderNumber(int orderNumber) {
		Optional<DishesEntity> dish = dishesRepository.findByOrderNumber(orderNumber);
		if (dish.isPresent()) {
			return dish.get();
		} else {
			DishesEntity emptyDish = new DishesEntity();
			return emptyDish;
		}
	}

	// 注文番号に応じて商品名を検索
	public String getDishName(int orderNumber) {
		return dishesRepository.findByOrderNumber(orderNumber)
				.map(DishesEntity::getDishName).orElse("");
	}

	// 注文番号に応じて商品idを検索
	public Integer getDishId(int orderNumber) {
		return dishesRepository.findByOrderNumber(orderNumber)
				.map(DishesEntity::getDishId).orElse(null);
	}

	// 注文番号に応じて値段を検索
	public Integer getPrice(int orderNumber) {
		return dishesRepository.findByOrderNumber(orderNumber)
				.map(DishesEntity::getPrice).orElse(null);
	}

	// 注文番号に応じて注文フラグを取得
	public boolean getOnSaleFlg(int orderNumber) {
		return dishesRepository.findByOrderNumber(orderNumber)
				.map(DishesEntity::isOnSaleFlg).orElse(null);
	}

	// 座席番号と食事中フラグから利用者を検索
	public List<PassengersEntity> getPassengerBySeatNumberAndEatingFlg(Integer seatNumber, boolean eatingFlg) {
		return passengersRepository.findBySeatNumberAndEatingFlg(seatNumber, eatingFlg).stream() // Optional を Stream に変換
				.toList(); // Stream を List に変換 (Java 16+);
	}

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

	// 座席番号をセッションから取得してモデル追加
	public Integer addPassengerId(Model model, HttpSession session) {
		Integer passengerId = (Integer) session.getAttribute("passengerId");
		System.out.println("セッションpassengerId：" + passengerId);
		if (passengerId != null) {
			model.addAttribute("passengerId", passengerId);
		} else {
			model.addAttribute("passengerId", "未設定");
		}
		return passengerId;
	}

	// 利用客IDから注文履歴を取得
	public OrderRecordDtoList restOrderRecord(Integer passengerId, HttpSession session) {
		OrderRecordDtoList orderRecordDtoList = new OrderRecordDtoList();
		List<OrderRecordDto> orderRecordList = new ArrayList<>();
		Integer totalPrice = 0;
		// DateTimeFormatterを使ってフォーマットを指定
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		List<OrdersEntity> OrdersList = ordersRepository.findByPassengerId(passengerId);
		for (OrdersEntity Order : OrdersList) {
			LocalTime orderTime = Order.getOrderTime();
			List<OrderDetailsEntity> OrderDetailsList = orderDetailsRepository.findByOrderId(Order.getOrderId());
			for (OrderDetailsEntity OrderDetails : OrderDetailsList) {
				OrderRecordDto orderRecordDto = new OrderRecordDto();
				orderRecordDto.setId(OrderDetails.getId());
				orderRecordDto.setOrderId(Order.getOrderId());
				String dishName = OrderDetails.getDishName();
				Integer quantity = OrderDetails.getQuantity();
				orderRecordDto.setDishName(dishName);
				System.out.println("dishName："+dishName);
				// 詳細テーブルには値段の欄がないので検索、すでに商品テーブルに存在しないとぬるぽ
				Integer price = dishesRepository.findByDishName(dishName).getPrice();
				orderRecordDto.setPrice(price);
				orderRecordDto.setQuantity(quantity);
				orderRecordDto.setUndeliveredFlg(OrderDetails.isUndeliveredFlg());
				orderRecordDto.setOrderTime(orderTime.format(formatter));
				orderRecordList.add(orderRecordDto);
				totalPrice += price * quantity;
			}
		}

		orderRecordDtoList.setOrderRecordDtoList(orderRecordList);
		orderRecordDtoList.setTotalPrice(totalPrice);
		session.setAttribute("orderRecord", orderRecordDtoList);

		return orderRecordDtoList;
	}

	// 注文リストを取得
	public static List<OrderDetailsDto> getCartContents(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<OrderDetailsDto> orderDetails = (List<OrderDetailsDto>) session.getAttribute("Cart");
		// 初回はモデルがないのでorderDetailListはnull
		if (orderDetails == null) {
			orderDetails = new ArrayList<>();
		}
		return orderDetails;
	}

	// 注文番号と数量に応じて買い物かごに入れる
	public String addToCart(Integer orderNumber, Integer quantity, HttpSession session) {
		System.out.println("注文番号：" + orderNumber + ",数量：" + quantity);
		List<OrderDetailsDto> orderDetails = getCartContents(session);

		// 既存のorderNumberを検索
		boolean found = false;
		for (OrderDetailsDto existingOrder : orderDetails) {
			if (existingOrder.getOrderNumber().equals(orderNumber)) {
				// 既存の注文が見つかった場合、数量を更新
				existingOrder.setQuantity(quantity);
				found = true;
				System.out.println("買い物かご：" + orderDetails);
				break;
			}
		}

		// 既存の注文が見つからなかった場合のみ、新しい要素を追加
		if (!found) {
			OrderDetailsDto orderDetail = new OrderDetailsDto();
			orderDetail.setOrderNumber(orderNumber);
			orderDetail.setQuantity(quantity);
			orderDetail.setPrice(getPrice(orderNumber));
			String dishName = getDishName(orderDetail.getOrderNumber());
			orderDetail.setDishName(dishName);
			orderDetails.add(orderDetail);
			System.out.println("買い物かご：" + orderDetails);
			session.setAttribute("Cart", orderDetails);
		}
		return "order/choice";
	}

	// 表示ボタンで注文リストを表示する
	public List<OrderDetailsDto> viewCart(Model model, HttpSession session) {
		List<OrderDetailsDto> orderDetails = getCartContents(session);
		OrderDetailsDtoList orderDetailList = new OrderDetailsDtoList();
		orderDetailList.setOrderDetailsDtoList(orderDetails);
		model.addAttribute("orderDetailList", orderDetailList);
		System.out.println("買い物かごの表示：" + orderDetailList);
		return orderDetails;
	}

	// 注文した商品を取り消す
	public String removeFromCart(String dishName, Model model,
			HttpSession session) {
		List<OrderDetailsDto> orderDetails = getCartContents(session);

		for (OrderDetailsDto removeOrder : orderDetails) {
			if (removeOrder.getDishName().equals(dishName)) {
				orderDetails.remove(removeOrder);
				break;
			}
		}
		session.setAttribute("Cart", orderDetails);
		System.out.println("注文リストから" + dishName + "を除外");
		return "order/choice";
	}

	// 商品の数量を確認画面で訂正する
	public void amendment(HttpSession session, Integer orderNumber, Integer quantity) {
		List<OrderDetailsDto> orderDetails = ChoiceService.getCartContents(session);
		for (OrderDetailsDto amendmentOrder : orderDetails) {
			if (amendmentOrder.getOrderNumber().equals(orderNumber)) {
				amendmentOrder.setQuantity(quantity);
				break;
			}
		}
		session.setAttribute("Cart", orderDetails);
	}

	// 座席番号から利用客テーブルの検索または登録を行う。
	public PassengersEntity registPassenger(Integer seatNumber, Model model, HttpSession session) {
		// 全利用客情報を取得
		List<PassengersEntity> passengers = getPassengerBySeatNumberAndEatingFlg(seatNumber, true);
		// 指定座席に食事中の人がいない時だけ利用者登録
		if (passengers.isEmpty()) {

			LocalTime currentTime = LocalTime.now();
			PassengersEntity passengersEntity = new PassengersEntity();
			passengersEntity.setSeatNumber(seatNumber);
			passengersEntity.setStartTime(currentTime);
			passengersEntity.setUndeliveredFlg(true);
			passengersEntity.setEatingFlg(true);
			passengersRepository.saveAndFlush(passengersEntity);
			Integer passengerId = passengersEntity.getPassengerId();
			System.out.println("利用者登録passengerId：" + passengerId);
			session.setAttribute("passengerId", passengerId);
			addPassengerId(model, session);

			return passengersEntity;

		} else if (passengers.size() == 1) {
			PassengersEntity passenger = passengers.get(0);
			passenger.setUndeliveredFlg(true);
			return passenger; // 食事中の人がいたらその人を取得
		} else {
			throw new MultiplePassengersException("指定された座席に複数の利用者がいます。");
		}
	}

	// 座席番号で注文テーブルに登録
	public OrdersEntity insertOrders(Integer seatNumber, Model model, HttpSession session) {
		LocalTime currentTime = LocalTime.now();
		// 注文テーブルに追加
		OrdersEntity ordersEntity = new OrdersEntity();
		// 初回の場合、利用者登録も行う
		ordersEntity.setPassengerId(registPassenger(seatNumber, model, session).getPassengerId());
		ordersEntity.setSeatNumber(seatNumber);
		ordersEntity.setOrderTime(currentTime);
		ordersEntity.setUndeliveredFlg(true);
		ordersRepository.saveAndFlush(ordersEntity);
		return ordersEntity;
	}

	// 注文詳細DTOと注文IDの内容で注文詳細テーブルに登録
	public Integer insertOrderDetails(OrderDetailsDto order, Integer orderId) {
		// 外部キー制約により、注文テーブルのIDをセット
		OrderDetailsEntity orderDetailsEntity = new OrderDetailsEntity();
		orderDetailsEntity.setOrderId(orderId);
		orderDetailsEntity.setDishId(getDishId(order.getOrderNumber()));
		orderDetailsEntity.setDishName(getDishName(order.getOrderNumber()));
		orderDetailsEntity.setQuantity(order.getQuantity());
		orderDetailsEntity.setUndeliveredFlg(true);
		OrderDetailsEntity saveEntity = orderDetailsRepository.saveAndFlush(orderDetailsEntity);
		return saveEntity.getId();
	}

	// 今までの注文履歴を取得
	public OrderRecordDtoList getOrderRecords(HttpSession session) {
		OrderRecordDtoList orderRecordDtoList = (OrderRecordDtoList) session.getAttribute("orderRecord");

		// 初注文のとき
		if (orderRecordDtoList == null) {
			orderRecordDtoList = new OrderRecordDtoList();
			List<OrderRecordDto> EmptyRecordList = new ArrayList<>();
			orderRecordDtoList.setOrderRecordDtoList(EmptyRecordList);
			orderRecordDtoList.setTotalPrice(0);
			System.out.println("初注文：" + orderRecordDtoList);
		}
		return orderRecordDtoList;
	}

	// お会計の際食事中フラグをオフ,食事終了時間を挿入
	public void restaurantBill(Integer passengerId) {
		LocalTime currentTime = LocalTime.now();
		PassengersEntity passenger = passengersRepository.findByPassengerId(passengerId);
		passenger.setEndTime(currentTime);
		passenger.setEatingFlg(false);
		passengersRepository.saveAndFlush(passenger);
	}
}
