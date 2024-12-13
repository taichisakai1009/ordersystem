package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.PassengersEntity;

import jakarta.transaction.Transactional;

public interface PassengersRepository extends JpaRepository<PassengersEntity, Integer> {
	List<PassengersEntity> findBySeatNumberAndEatingFlg(Integer seatNumber, boolean eatingFlg);
	
    @Query("SELECT p.passengerId FROM PassengersEntity p WHERE p.seatNumber = :seatNumber AND p.eatingFlg = :eatingFlg")
    List<Integer> findPassengerIdsBySeatNumberAndEatingFlg(@Param("seatNumber") Integer seatNumber, @Param("eatingFlg") boolean eatingFlg);
	
	PassengersEntity findByPassengerId(Integer passengerId);
	
    @Modifying
    @Transactional
    @Query("UPDATE PassengersEntity o SET o.undeliveredFlg = false WHERE o.passengerId = :passengerId")
    int updateUndeliveredFlgByPassengerId(@Param("passengerId") Integer passengerId);
   
}
