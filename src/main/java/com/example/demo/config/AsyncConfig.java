package com.example.demo.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    // 非同期処理で利用するスレッドプールの設定
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // コアスレッド数（常に稼働するスレッド数）
        executor.setCorePoolSize(5);
        // 最大スレッド数（負荷が高いときに増えるスレッド数）
        executor.setMaxPoolSize(10);
        // キューのキャパシティ（タスクが待機できる数）
        executor.setQueueCapacity(500);
        // スレッドの名前のプレフィックス（デバッグ用に役立つ）
        executor.setThreadNamePrefix("AsyncExecutor-");
        // スレッドプールの初期化
        executor.initialize();
        return executor;
    }
}

