package com.example.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;

import jakarta.transaction.Transactional;

@Service
public class ClerkDetailsService implements UserDetailsService {
    private final ClerksRepository clerksRepository;

    public ClerkDetailsService(ClerksRepository clerksRepository) {
        this.clerksRepository = clerksRepository;
    }
    
    @Transactional // RolesEntityのclerksプロパティ（コレクション）を遅延ロードしようとした際にHibernateのセッションが閉じられており、データベースアクセスができなくなった。
    @Override      // @OneToManyにおける遅延ロードは、デフォルトで有効になっています。
    public UserDetails loadUserByUsername(String clerkNumber) throws UsernameNotFoundException {
        ClerksEntity clerk = clerksRepository.findByClerkNumber(Integer.parseInt(clerkNumber))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("clerk:" + clerk);
        return new ClerkDetails(clerk);
    }
}