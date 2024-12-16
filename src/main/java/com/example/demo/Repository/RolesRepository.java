package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.RolesEntity;

public interface RolesRepository extends JpaRepository<RolesEntity, Integer>  {

}
