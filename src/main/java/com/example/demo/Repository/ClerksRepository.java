package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.ClerksEntity;

import jakarta.transaction.Transactional;

public interface ClerksRepository extends JpaRepository<ClerksEntity, Integer> {

	Optional<ClerksEntity> findByClerkNumberAndPassword(Integer clerkNumber, String password);
	
	Optional<ClerksEntity> findByClerkNumber(Integer clerkNumber);
	
	List<ClerksEntity> findByNameContaining(String name);
	
    @Modifying
    @Transactional
    @Query("UPDATE ClerksEntity c SET c.name = :name, c.mailAddress = :mailAddress, c.tel = :tel WHERE c.clerkId = :clerkId")
    int updateClerkDetails(@Param("clerkId") Integer clerkId, 
                           @Param("name") String name, 
                           @Param("mailAddress") String mailAddress, 
                           @Param("tel") String tel);
}