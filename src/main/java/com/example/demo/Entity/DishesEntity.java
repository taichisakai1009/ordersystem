package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dishes")
public class DishesEntity {

	// 料理ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer dishId;
	
	// 料理名
	private String dishName;
	
	// 注文番号
	private Integer orderNumber;
	
	// 値段
	private Integer price;
}
