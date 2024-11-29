package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.DishesEntity;

public interface DishesRepository extends JpaRepository<DishesEntity, Integer> {

	Optional<DishesEntity> findByOrderNumber(int orderNumber);
	
	DishesEntity findByDishName(String dishName);
	}