package com.example.demo.Dto;

import lombok.Data;

@Data
public class SeeDishesDto {

	// 料理ID
	private Integer dishId;
	
	// 料理名
	private String dishName;
	
	// 注文番号
	private Integer orderNumber;
	
	// 値段
	private Integer price;
	
	// 提供中フラグ
	private boolean onSaleFlg;
	
	// 料理の総数
	private Integer numberDishes;
	
	// 提供中の料理の総数
	private Integer numberOnSaleDishes;
	
	// 提供中止中の料理の総数
	private Integer numberNotOnSqleDishes;
}
