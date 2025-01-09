package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.CommentsEntity;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Integer> {
    // 追加のクエリメソッドがあればここに記述
}
