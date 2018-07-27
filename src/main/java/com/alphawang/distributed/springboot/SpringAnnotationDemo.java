package com.alphawang.distributed.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
@Configuration
public class SpringAnnotationDemo {

    public static void main(String[] args) {
        /**
         * XML 配置文件驱动 `ClassPathXmlApplicationContext`
         */
        ClassPathXmlApplicationContext xmlApplicationContext;

        /**
         * 注解驱动上下文 `AnnotationConfigApplicationContext`：用于找BeanDefinition
         * @since Spring Framework 3.0 开始引入的
         */
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册一个Configuration class
        context.register(SpringAnnotationDemo.class);
        context.refresh();

        /**
         * 对比 com.alphawang.distributed.DistributedApplication#main
         */
        // 有异常？ no
        log.info("get bean {}", context.getBean(SpringAnnotationDemo.class));
    }
}
    
