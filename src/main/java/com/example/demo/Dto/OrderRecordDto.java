package com.example.demo.Dto;

import lombok.Data;

@Data
public class OrderRecordDto {
	
	// 注文詳細ID
	private Integer id;
	
	// orders(注文テーブル)のID
	private Integer orderId;

	// 商品名
	private String dishName;
	
	// 値段
	private Integer price;

	// 数量
	private Integer quantity;

	// 注文時間
	private String orderTime;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
}
