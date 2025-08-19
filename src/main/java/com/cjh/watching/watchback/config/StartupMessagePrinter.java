package com.cjh.watching.watchback.config;

/**
 * - @author Cjh。
 * - @date 2025/8/15 11:21。
 **/

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupMessagePrinter implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎬 影视记录系统启动成功！");
        System.out.println("🎥 欢迎使用WatchBack影视记录系统");
        System.out.println("📽️  正在加载您的影视收藏...");
        System.out.println("🍿 系统已准备就绪，开始记录您的观影时光");
        System.out.println("📺 Enjoy your movie journey!");
    }
}

