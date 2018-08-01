package com.alphawang.distributed.springboot.springapplication.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
@EnableAutoConfiguration
public class SpringBootEventDemo {

    public static void main(String[] args) {
        /**
         * 若不加@EnableAutoConfiguration
         * 
         * - 监听到事件 org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
         * - 监听到事件 org.springframework.boot.context.event.ApplicationPreparedEvent
         * - 监听到事件 org.springframework.boot.context.event.ApplicationFailedEvent
         */

        /**
         * ApplicationStartingEvent（1）
         * ApplicationEnvironmentPreparedEvent（2）
         * ApplicationPreparedEvent（3）
         * ContextRefreshedEvent
         * ServletWebServerInitializedEvent
         * ApplicationStartedEvent（4）
         * ApplicationReadyEvent（5）
         * ContextClosedEvent
         * ApplicationFailedEvent (特殊情况)（6）
         * 
         */
        new SpringApplicationBuilder(SpringBootEventDemo.class)
            // 监听器
            .listeners(new ApplicationListener<ApplicationEvent>() {
                @Override 
                public void onApplicationEvent(ApplicationEvent event) {
                    log.info("监听到事件 {}", event.getClass().getName());
                }
            })
            // 启动
            .run(args);
    }
}
