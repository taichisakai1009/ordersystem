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
@Table(name = "orders")
public class OrdersEntity {

	// 	注文ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	
    // PassengersEntityとのリレーションを定義
//    @ManyToOne(fetch = FetchType.LAZY)  // 遅延ローディングを使用
//    @JoinColumn(name = "passenger_id", referencedColumnName = "passenger_id", nullable = false)
//    
	
	// 利用客ID
	private Integer passengerId;
	
	// 座席番号
	private Integer seatNumber;
	
	// 注文時間
	private LocalTime orderTime;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
}