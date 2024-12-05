package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.PassengersEntity;

import jakarta.transaction.Transactional;

public interface PassengersRepository extends JpaRepository<PassengersEntity, Integer> {
	Optional<PassengersEntity> findBySeatNumberAndEatingFlg(Integer seatNumber, boolean eatingFlg);
	
	PassengersEntity findByPassengerId(Integer passengerId);
	
    @Modifying
    @Transactional
    @Query("UPDATE PassengersEntity o SET o.undeliveredFlg = false WHERE o.passengerId = :passengerId")
    int updateUndeliveredFlgByPassengerId(@Param("passengerId") Integer passengerId);
    
//    @Modifying
//    @Transactional
//    @Query("UPDATE PassengersEntity p SET p.eatingFlg = false WHERE p.passengerId = :passengerId")
//    int updateEatingFlgByPassengerId(@Param("passengerId") Integer passengerId);

}
