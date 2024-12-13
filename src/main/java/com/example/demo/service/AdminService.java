package com.example.demo.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;

@Service
public class AdminService {

	private final ClerksRepository clerksRepository;

	AdminService(ClerksRepository clerksRepository) {
		this.clerksRepository = clerksRepository;
	}

	
	public List<ClerksEntity> findClerksByNumber(Integer clerkNumber) {
		return clerksRepository.findByClerkNumber(clerkNumber)
				.map(Collections::singletonList) // OptionalをListに変換
				.orElse(Collections.emptyList()); // 値が存在しない場合は空のリストを返す
	}
	
	public List<ClerksEntity> findByNameContaining(String name) {
		return clerksRepository.findByNameContaining(name);
	}
	
	public void updateClerkDetails(Integer clerkId, String name, String mailAddress, String tel) {
		clerksRepository.updateClerkDetails(clerkId, name, mailAddress, tel);
	}

}
