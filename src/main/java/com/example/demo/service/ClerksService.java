package com.example.demo.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.Dto.SeeClerksDto;
import com.example.demo.Dto.SeeOrdersDto;
import com.example.demo.Dto.SeePassengersDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.OrdersEntity;
import com.example.demo.Entity.PassengersEntity;
import com.example.demo.Repository.ClerksRepository;
import com.example.demo.Repository.OrderDetailsRepository;
import com.example.demo.Repository.OrdersRepository;
import com.example.demo.Repository.PassengersRepository;

@Service
public class ClerksService {

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	PassengersRepository passengersRepository;

	@Autowired
	ClerksRepository clerksRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	// エンティティを元にDtoにセット List<PassengersEntity>→List<SeePassengersDto>
	public List<SeePassengersDto> setSeePassengersDtoList(List<PassengersEntity> passengers) {
		List<SeePassengersDto> seePassengersDtoList = new ArrayList<>();
		for (PassengersEntity passengersEntity : passengers) {
			SeePassengersDto seePassengersDto = new SeePassengersDto();
			Integer passengerId = passengersEntity.getPassengerId();
			seePassengersDto.setPassengerId(passengerId);
			seePassengersDto.setSeatNumber(passengersEntity.getSeatNumber());
			seePassengersDto.setStartTime(passengersEntity.getStartTime());
			seePassengersDto.setEndTime(passengersEntity.getEndTime());
			seePassengersDto.setUndeliveredFlg(passengersEntity.isUndeliveredFlg());
			seePassengersDto.setEatingFlg(passengersEntity.isEatingFlg());
			Integer numberOrdered = ordersRepository.findByPassengerId(passengerId).size();
			seePassengersDto.setNumberOrdered(numberOrdered);
			Integer numberUndelivered = ordersRepository.findByPassengerIdAndUndeliveredFlg(passengerId, false).size();
			seePassengersDto.setNumberUndelivered(numberUndelivered);
			seePassengersDtoList.add(seePassengersDto);
		}
		return seePassengersDtoList;
	}

	// エンティティを元にDtoにセット List<OrdersEntity>→List<SeeOrdersDto>
	public List<SeeOrdersDto> setSeeOrdersDtoList(List<OrdersEntity> orders) {
		List<SeeOrdersDto> seeOrdersDtoList = new ArrayList<>();
		for (OrdersEntity seeOrdersEntity : orders) {
			SeeOrdersDto seeOrdersDto = new SeeOrdersDto();
			Integer orderId = seeOrdersEntity.getOrderId();
			seeOrdersDto.setOrderId(orderId);
			seeOrdersDto.setPassengerId(seeOrdersEntity.getPassengerId());
			seeOrdersDto.setSeatNumber(seeOrdersEntity.getSeatNumber());
			seeOrdersDto.setOrderTime(seeOrdersEntity.getOrderTime());
			seeOrdersDto.setUndeliveredFlg(seeOrdersEntity.isUndeliveredFlg());
			Integer numberOrderDetails = orderDetailsRepository.findByOrderId(orderId).size();
			seeOrdersDto.setNumberOrderDetails(numberOrderDetails);
			Integer numberUndelivered = orderDetailsRepository.findByOrderIdAndUndeliveredFlg(orderId, false).size();
			seeOrdersDto.setNumberUndelivered(numberUndelivered);
			seeOrdersDtoList.add(seeOrdersDto);
		}
		return seeOrdersDtoList;
	}

	// エンティティを元にDtoにセット List<ClerksEntity>→List<SeeClerksDto>
	public List<SeeClerksDto> setSeeClerksDtoList(List<ClerksEntity> clerks) {
		List<SeeClerksDto> seeClerksDtoList = new ArrayList<>();
		for (ClerksEntity seeClerksEntity : clerks) {
			SeeClerksDto seeClerksDto = new SeeClerksDto();
			seeClerksDto.setClerkId(null);
			seeClerksDto.setName(seeClerksEntity.getName());
			seeClerksDto.setClerkNumber(seeClerksEntity.getClerkNumber());
			seeClerksDto.setPassword(seeClerksEntity.getPassword());
			seeClerksDto.setMailAddress(seeClerksEntity.getMailAddress());
			seeClerksDto.setTel(seeClerksEntity.getTel());
			seeClerksDto.setStartDate(seeClerksEntity.getStartDate());
			String roleName = seeClerksEntity.getRole().getName();
			seeClerksDto.setRoleName(roleName);
			seeClerksDtoList.add(seeClerksDto);
		}
		return seeClerksDtoList;
	}

	public ClerksEntity findClerksByNumber(Integer clerkNumber) {

		ClerksEntity clerk = clerksRepository.findByClerkNumber(clerkNumber);
		if (clerk == null) {
			ClerksEntity Emptyclerk = new ClerksEntity();
			return Emptyclerk;
		}
		return clerk;
	}

	public ClerksEntity findByMailaddress(String mailaddress) {

		ClerksEntity clerk = clerksRepository.findByMailAddress(mailaddress);
		//		if (clerk == null) {
		//			ClerksEntity Emptyclerk = new ClerksEntity();
		//			return Emptyclerk;
		//		}
		return clerk;
	}

	public ClerksEntity findByClerkId(Integer clerkId) {
		ClerksEntity clerk = clerksRepository.findByClerkId(clerkId);
		if (clerk == null) {
			ClerksEntity Emptyclerk = new ClerksEntity();
			return Emptyclerk;
		}
		return clerk;
	}

	public List<ClerksEntity> findByNameContaining(String name) {
		return clerksRepository.findByNameContaining(name);
	}

	// パスワードの変更ができるか判定
	public boolean changePassword(ClerksEntity clerk, String currentPassword, String newPassword,
			String confirmPassword,
			Model model) {
		boolean changePasswordFlg = true;
		if (!newPassword.equals(confirmPassword)) {
			String mismatchError = "確認用パスワードと一致しません。";
			model.addAttribute("mismatchError", mismatchError);
			changePasswordFlg = false;
		}
		String hashPassword = clerk.getPassword();
		if (!passwordEncoder.matches(currentPassword, hashPassword)) {
			String isNotCurrentPassword = "現在のパスワードが間違えています。";
			model.addAttribute("isNotCurrentPassword", isNotCurrentPassword);
			changePasswordFlg = false;
		}
		if (passwordEncoder.matches(newPassword, hashPassword)) {
			String isNotNewPassword = "新しいパスワードは現在のパスワードと異なる必要があります。";
			model.addAttribute("isNotNewPassword", isNotNewPassword);
			changePasswordFlg = false;
		}
		if (changePasswordFlg) {
			clerk.setPassword(passwordEncoder.encode(newPassword));
			clerksRepository.save(clerk);
			String changePasswordConfirm = "パスワード変更が完了しました。";
			model.addAttribute("changePasswordConfirm", changePasswordConfirm);
		}
		return changePasswordFlg;
	}

	// 店員番号をもとに、is_first_loginフラグをオフにする
	public void updateIsFirstLoginToFalseByClerkNumber(Integer clerkNumber) {
		clerksRepository.updateIsFirstLoginToFalseByClerkNumber(clerkNumber);
	}

	// 店員番号をもとにClerkモデルをセットする
	public void addClerkModel(Integer clerkNumber, Model model) {
		ClerksEntity clerk = clerksRepository.findByClerkNumber(clerkNumber);
		model.addAttribute("clerk", clerk);
	}

	// メールアドレスと一致するデータがあるか否か
	public boolean existsByMailAddress(String mailAddress) {
		return clerksRepository.existsByMailAddress(mailAddress);
	}

	//　id を元にメールアドレスを取得
	public String getMailAddressByClerkId(Integer clerkId) {
		return clerksRepository.findMailAddressByClerkId(clerkId);
	}

	// メールアドレスをもとにidを取得
	public Integer findClerkIdByMailAddress(String mailAddress) {
		Integer clerkId = clerksRepository.findClerkIdByMailAddress(mailAddress);
		if (clerkId == null) {
			return 0;
		}
		return clerkId;
	}

	// 一時間ごとの利用客数を取得
	public String[] getCongestion() {

		// StringBuilderを使ってカンマ区切りの文字列を作成
		StringBuilder times = new StringBuilder();
		StringBuilder counts = new StringBuilder();

		for (int i = 9; i < 18; i++) {
			// xをLocalTime型に変換
			LocalTime start = LocalTime.of(i, 0, 0); // x:00:00
			LocalTime end = LocalTime.of(i + 1, 0, 0); // y:00:00
			int count = passengersRepository.countByStartTimeBetween(start, end);
			if (i > 9) {
				times.append(",");
				counts.append(",");
			}
			times.append(LocalTime.of(i, 30, 0));
			counts.append(count);
		}
		String xData = times.toString();
		String yData = counts.toString();
		String[] Data = { xData, yData };
		return Data;
	}
	
	public void deleteAllPassengers() {
		passengersRepository.deleteAll();
	}

	// エンティティを元にDtoにセット List<DishesEntity>→List<SeeDishesDto>
	//	public List<SeeDishesDto> setSeeDishesDtoList(List<DishesEntity> dishes) {
	//		List<SeeDishesDto> seeDishesDtoList = new ArrayList<>();
	//		for (DishesEntity dish : dishes) {
	//			SeeDishesDto seeDishesDto = new SeeDishesDto();
	//			seeDishesDto.setDishId(dish.getDishId());
	//			seeDishesDto.setDishName(dish.getDishName());			
	//			seeDishesDto.setOrderNumber(dish.getOrderNumber());
	//			seeDishesDto.setPrice(dish.getPrice());
	//			seeDishesDto.setOnSaleFlg(dish.isOnSaleFlg());
	//		}
	//		return seeDishesDtoList;
	//	}
}
