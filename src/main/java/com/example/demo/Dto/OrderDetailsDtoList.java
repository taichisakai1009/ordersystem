package com.example.demo.Dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderDetailsDtoList {
	// 注文詳細DTOリスト
	private List<OrderDetailsDto> orderDetailsDtoList;
	// 座席番号
	private Integer seatNumber;
}
