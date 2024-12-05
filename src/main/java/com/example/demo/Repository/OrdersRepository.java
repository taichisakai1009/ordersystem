package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.OrdersEntity;

import jakarta.transaction.Transactional;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Integer> {
	
	List<OrdersEntity> findByPassengerId(Integer passengerId);
	
	List<OrdersEntity> findByPassengerIdAndUndeliveredFlg(Integer passengerId, boolean undeliveredFlg);
	
    @Modifying
    @Transactional
    @Query("UPDATE OrdersEntity o SET o.undeliveredFlg = false WHERE o.orderId = :orderId")
    int updateUndeliveredFlgByOrderId(@Param("orderId") Integer orderId);
}