package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.OrdersEntity;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Integer> {

}
