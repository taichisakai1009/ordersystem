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

	//	@Autowired
	//	private CustomUserDetailsService customuserDetailsService;
	//	@Autowired
	//	private UserDetailsService userDetailsService;
	@Autowired
	private ClerkDetailsService clerkDetailsService;
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	//	new CustomAuthenticationSuccessHandler()
	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(clerkDetailsService)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.formLogin(login -> login
						.loginPage("/login/login?show=true").permitAll()
						.loginProcessingUrl("/in") // 入力フォームのth:actionと分けなければいけない
						//						.defaultSuccessUrl(new CustomAuthenticationSuccessHandler())
						.successHandler(customAuthenticationSuccessHandler) // カスタムハンドラを設定
						.failureUrl("/login/login?show=true&error=true") // ログイン失敗後のリダイレクト先
						.usernameParameter("clerkNumber")
						.passwordParameter("password"))
				.logout(logout -> logout // ログアウト
						.logoutUrl("/logout") // ログアウトのURL
						.logoutSuccessUrl("/login/login?logout=true") // ログアウト成功後のリダイレクト先
						//						.addLogoutHandler(customLogoutHandler()) // カスタムログアウトハンドラを追加
						.invalidateHttpSession(true) // セッションの無効化
						.deleteCookies("JSESSIONID") // Cookieの削除
				)
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/").permitAll() // タイトルを初期表示する
						.requestMatchers("/title/**").permitAll() // タイトルに戻る
						.requestMatchers("/order/**").permitAll()
						.requestMatchers("/login/**").permitAll()
						.requestMatchers("/two-factor-auth/**").permitAll()
						.requestMatchers("/chat/**").permitAll()
						.requestMatchers("/css/**").permitAll() // css
						.requestMatchers("/js/**").permitAll() // js
						.requestMatchers("/actuator/beans").permitAll() // Bean確認URL
						.requestMatchers("/clerks/**").authenticated() // /clerks/** パスは認証が必要
						.requestMatchers("/admin/**").hasRole("Admin") // Admin権限が必要
						.anyRequest().authenticated());

		return http.build();
	}

	//    @Bean
	//    public LogoutHandler customLogoutHandler() {
	//        return new CustomLogoutHandler();
	//    }

}