package com.alphawang.distributed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@SpringBootApplication 
public class DistributedApplication {

    public static void main(String[] args) {
        /**
         * SpringApplication Spring Boot 驱动 Spring 应用上下文的引导类
         */
        
        /**
         * 启动方法 
         */
        SpringApplication.run(DistributedApplication.class, args);
        

        /**
         * 自定义SpringApplication : 1
         * 
         * SpringApplicationBuilder
         */
        new SpringApplicationBuilder(DistributedApplication.class)
            .properties("server.port=0")
            .run(args);

        /**
         * 自定义SpringApplication : 2
         * 
         * SpringApplication  --不方便
         */
        SpringApplication springApplication = new SpringApplication(DistributedApplication.class);
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("server.port", 0);
        springApplication.setDefaultProperties(properties);

        /**
         * springApplication.run 返回context
         */
        ConfigurableApplicationContext context = springApplication.run(args);
        log.info("get bean {}", context.getBean(DistributedApplication.class));
    }
}
