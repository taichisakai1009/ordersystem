package com.example.demo.Dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class SeePassengersDto {
	
	// 利用客ID
	private Integer passengerId;
	
	// 座席番号
	private Integer seatNumber;
	
	// 食事開始時間
	private LocalTime startTime;
	
	// 食事終了時間
	private LocalTime endTime;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
	
	// 食事中か食事後か
	private boolean eatingFlg;
	
	// 注文件数
	private Integer numberOrdered;
	
	// 未配達注文数
	private Integer numberUndelivered;
}
