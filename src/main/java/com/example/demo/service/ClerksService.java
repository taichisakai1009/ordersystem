package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.SeeClerksDto;
import com.example.demo.Dto.SeeOrdersDto;
import com.example.demo.Dto.SeePassengersDto;
import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Entity.OrdersEntity;
import com.example.demo.Entity.PassengersEntity;
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
