package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.PassengersEntity;

public interface PassengersRepository extends JpaRepository<PassengersEntity, Integer> {
	Optional<PassengersEntity> findBySeatNumberAndEatingFlg(Integer seatNumber, boolean eatingFlg);

}
