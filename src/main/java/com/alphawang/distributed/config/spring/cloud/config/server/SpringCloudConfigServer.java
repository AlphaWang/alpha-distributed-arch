package com.alphawang.distributed.config.spring.cloud.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServer {

    /**
     * 启动成功后，访问
     * localhost:10086/config/default
     * or
     * localhost:10086/config/dev
     * 
     * default --> profile
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServer.class, args);
    }

    /**
     * 自定义：
     * 当没有EnvironmentRepository Bean时，会默认激活DefaultRepositoryConfiguration (Git实现)
     * 
     * @Bean
     * public MultipleJGitEnvironmentRepository defaultEnvironmentRepository
     */
    @Bean
    public EnvironmentRepository environmentRepository() {
        return new EnvironmentRepository() {
            /**
             * 如果url path是： /config/test/master 
             *                /${application}/${profile}/${label}
             *                
             * @param application : config
             * @param profile     : test
             * @param label       : master
             */
            @Override 
            public Environment findOne(String application, String profile, String label) {
                Environment env = new Environment("default", profile);
                List<PropertySource> propertyList = env.getPropertySources();
                
                Map<String, Object> source = new HashMap<>();
                source.put("name", "Alpha-2");
                PropertySource propertySource = new PropertySource("map", source);
                
                propertyList.add(propertySource);
                return env;
            }
        };
    }
}
