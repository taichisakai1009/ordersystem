package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// 通称「設定クラス」。
public class SecurityConfig implements WebMvcConfigurer {

	@Autowired
	private CustomUserDetailsService userDetailsService;
	
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@SuppressWarnings("removal")
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable() // これがないとアクセスできない。
				.formLogin(login -> login
						.loginPage("/login/login?show=true").permitAll()
						.loginProcessingUrl("/in") // 入力フォームのth:actionと分けなければいけない
						.defaultSuccessUrl("/clerks/login", true)
						.failureUrl("/login/login?show=true&error=true") // ログイン失敗後のリダイレクト先
						.usernameParameter("clerkNumber")
						.passwordParameter("password")
						)
				.logout(logout -> logout // ログアウト
						.logoutUrl("/logout") // ログアウトのURL
						.logoutSuccessUrl("/login/login?logout=true") // ログアウト成功後のリダイレクト先
						.invalidateHttpSession(true) // セッションの無効化
						.deleteCookies("JSESSIONID") // Cookieの削除
						)
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/").permitAll() // タイトルを初期表示する
						.requestMatchers("/title/**").permitAll() // タイトルに戻る
						.requestMatchers("/order/**").permitAll()
						.requestMatchers("/login/**").permitAll()
						.requestMatchers("/css/**").permitAll() // css
						.requestMatchers("/js/**").permitAll() // js
						.requestMatchers("/clerks/**").authenticated() // /clerks/** パスは認証が必要
						.anyRequest().authenticated()
						);

		return http.build();
	}

}