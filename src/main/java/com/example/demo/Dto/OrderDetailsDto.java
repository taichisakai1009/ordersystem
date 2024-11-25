package com.example.demo.Dto;

import java.io.Serializable;

import lombok.Data;

@Data
// オブジェクトの状態をバイトストリームに変換したり、もとに戻したりできる
// セッション属性に保存されるすべてのオブジェクトは、シリアライズ可能である必要がある。
public class OrderDetailsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 商品名
	private String dishName;
	
	// 注文番号
	private Integer orderNumber;

	// 数量
	private Integer quantity;
	
	// 値段
	private Integer price;
}
