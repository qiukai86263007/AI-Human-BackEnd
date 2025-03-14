package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
@EnableAsync
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("\n" +
                " █████  ██     ██   ██ ██    ██ ███    ███  █████  ███    ██ \n" +
                "██   ██ ██     ██   ██ ██    ██ ████  ████ ██   ██ ████   ██ \n" +
                "███████ ██  ██ ███████ ██    ██ ██ ████ ██ ███████ ██ ██  ██ \n" +
                "██   ██ ██     ██   ██ ██    ██ ██  ██  ██ ██   ██ ██  ██ ██ \n" +
                "██   ██ ██     ██   ██  ██████  ██      ██ ██   ██ ██   ████ \n" +
                "                                                             \n" +
                ":: AI-Human 数字人平台 ::        (v1.0.0)     启动成功          \n");
    }
}
