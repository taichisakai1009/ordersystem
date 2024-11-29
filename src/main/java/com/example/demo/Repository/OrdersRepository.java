package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.OrdersEntity;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Integer> {
	List<OrdersEntity> findByPassengerId(Integer passengerId);
}