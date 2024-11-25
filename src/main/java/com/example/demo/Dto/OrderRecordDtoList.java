package com.example.demo.Dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRecordDtoList {

	// 注文リスト
	private List<OrderRecordDto> orderRecordDtoList;
	
	// 合計金額
	private Integer totalPrice;
}
