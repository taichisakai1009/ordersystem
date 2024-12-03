package com.example.demo.Dto;

import lombok.Data;

@Data
public class OrderRecordDto {
	
	// orders(注文テーブル)のID
	private Integer id;

	// 商品名
	private String dishName;

	// 数量
	private Integer quantity;

	// 注文時間
	private String orderTime;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
}
