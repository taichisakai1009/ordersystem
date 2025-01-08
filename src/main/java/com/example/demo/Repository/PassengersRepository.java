package com.example.demo.Repository;

import java.time.LocalTime;
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
    
    // start_timeがx:00:00からy:00:00の範囲にあるデータのカウントを取得
    @Query("SELECT COUNT(p) FROM PassengersEntity p WHERE p.startTime BETWEEN :x AND :y")
    int countByStartTimeBetween(LocalTime x, LocalTime y);

   
}
