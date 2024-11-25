package com.example.demo.Entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "passengers")
public class PassengersEntity {

	// 利用客ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer passengerId;
	
	// 座席番号
	private Integer seatNumber;
	
	// 食事開始時間
	private LocalTime startTime;
	
	// 食事終了時間
	private LocalTime endTime;
	
	// 食事中か食事後か
	private boolean eatingFlg;
}