package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.OrderDetailsEntity;

import jakarta.transaction.Transactional;

public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Integer> {
	
	List<OrderDetailsEntity> findByOrderId(Integer orderId);
	
	@Modifying
	@Transactional
    @Query("UPDATE OrderDetailsEntity o SET o.undeliveredFlg = false WHERE o.id = :id")
    int updateUndeliveredFlgToFalse(@Param("id") Integer id);
}
