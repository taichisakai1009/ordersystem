package com.example.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ClerksEntity;
import com.example.demo.Repository.ClerksRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class ClerkDetailsService implements UserDetailsService {
    private final ClerksRepository clerksRepository;
    private final HttpSession session;

    public ClerkDetailsService(ClerksRepository clerksRepository, HttpSession session) {
        this.clerksRepository = clerksRepository;
        this.session = session;
    }
    
    @Transactional // RolesEntityのclerksプロパティ（コレクション）を遅延ロードしようとした際にHibernateのセッションが閉じられており、データベースアクセスができなくなった。
    @Override      // @OneToManyにおける遅延ロードは、デフォルトで有効になっています。
    public UserDetails loadUserByUsername(String clerkNumber) throws UsernameNotFoundException {
        ClerksEntity clerk = clerksRepository.findByClerkNumber(Integer.parseInt(clerkNumber));           
        session.setAttribute("clerk", clerk);
        return new ClerkDetails(clerk);
    }
}