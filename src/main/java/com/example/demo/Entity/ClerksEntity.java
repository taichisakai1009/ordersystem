package com.example.demo.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "clerks")
public class ClerksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clerk_id")
    private Integer clerkId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "clerk_number", nullable = false, unique = true)
    private Integer clerkNumber;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "mail_address", nullable = false, length = 50)
    private String mailAddress;

    @Column(name = "tel", nullable = false, length = 15)
    private String tel;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.EAGER) // 即時ロードで権限を取得
    @JoinColumn(name = "role_id", nullable = false) // 外部キー「clerk_roles.role_id」
    private RolesEntity role;
    
    @Column(name = "is_first_login", nullable = false)
    private Boolean isFirstLogin = true; // 登録時デフォルトでオンに設定
}
