package com.example.demo.Dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class SeeOrdersDto {

	// 	注文ID
	private Integer orderId;
	
	// 利用客ID
	private Integer passengerId;
	
	// 座席番号
	private Integer seatNumber;
	
	// 注文時間
	private LocalTime orderTime;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
	
	// 注文件数
	private Integer numberOrderDetails;
	
	// 未配達注文数
	private Integer numberUndelivered;
}
