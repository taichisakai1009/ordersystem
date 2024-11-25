package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "order_details")
public class OrderDetailsEntity {

	// 注文詳細ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	// 注文ID
	private Integer orderId;
	
	// 料理ID
	private Integer dishId;
	
	// 料理名
	private String dishName;
	
	// 数量
	private Integer quantity;
	
	// 注文未配達フラグ
	private boolean undeliveredFlg;
}
