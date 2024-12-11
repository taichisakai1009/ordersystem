package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.ClerksEntity;

public interface ClerksRepository extends JpaRepository<ClerksEntity, Integer> {

	Optional<ClerksEntity> findByClerkNumberAndPassword(Integer clerkNumber, String password);
	
	Optional<ClerksEntity> findByClerkNumber(Integer clerkNumber);
	
	}