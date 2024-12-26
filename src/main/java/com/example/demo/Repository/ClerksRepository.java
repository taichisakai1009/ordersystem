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

	ClerksEntity findByClerkId(Integer clerkId);
	
	ClerksEntity findByClerkNumber(Integer clerkNumber);
	
	ClerksEntity findByMailAddress(String mailaddress);
	
	List<ClerksEntity> findByNameContaining(String name);
	
    @Modifying
    @Transactional
    @Query("UPDATE ClerksEntity c SET c.name = :name, c.mailAddress = :mailAddress, c.tel = :tel WHERE c.clerkId = :clerkId")
    int updateClerkDetails(@Param("clerkId") Integer clerkId, 
                           @Param("name") String name, 
                           @Param("mailAddress") String mailAddress, 
                           @Param("tel") String tel);
    
    void deleteByClerkId(Integer clerkId);
    
    boolean existsByClerkNumber(Integer clerkNumber); // 入力された店員番号と一致するデータがあったらtrue,無かったらfalse
    
    boolean existsByMailAddress(String mailAddress); // 入力されたメールアドレスと一致するデータがあったらtrue,なかったらfalse
    
    // @Queryでクエリ文を明示しないと、カラムをすべて検索してエンティティ型を返してしまった。
    @Query("SELECT c.mailAddress FROM ClerksEntity c WHERE c.clerkId = :clerkId")
    String findMailAddressByClerkId(Integer clerkId); // idに応じてメールアドレスを取得
    
    @Query("SELECT c.clerkId FROM ClerksEntity c WHERE c.mailAddress = :mailAddress")
    Integer findClerkIdByMailAddress(String mailAddress); // idに応じてメールアドレスを取得
    
    @Transactional
    @Modifying
    @Query("UPDATE ClerksEntity c SET c.password = :password WHERE c.mailAddress = :mailAddress")
    int updatePasswordByMailAddress(@Param("password") String password, @Param("mailAddress") String mailAddress);
    
    @Transactional
    @Modifying
    @Query("UPDATE ClerksEntity c SET c.password = :password WHERE c.clerkId = :clerkId")
    int updatePasswordByClerkId(@Param("password") String password, @Param("clerkId") Integer clerkId);
    
    // clerk_number に一致する is_first_login を false に更新
    @Transactional
    @Modifying
    @Query("UPDATE ClerksEntity c SET c.isFirstLogin = false WHERE c.clerkNumber = :clerkNumber")
    int updateIsFirstLoginToFalseByClerkNumber(Integer clerkNumber);


}