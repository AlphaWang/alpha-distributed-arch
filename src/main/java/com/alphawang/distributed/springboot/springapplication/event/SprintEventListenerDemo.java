package com.alphawang.distributed.springboot.springapplication.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
public class SprintEventListenerDemo {

    /**
     * 发送 Spring 事件通过  ApplicationEventMulticaster#multicastEvent(ApplicationEvent, ResolvableType)
     * @param args
     */
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();

        
        // 添加事件监听
        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override 
            public void onApplicationEvent(ApplicationEvent event) {
                log.info("收到事件 {}", event);
            }
        });
        
        // 添加自定义监听器
        context.addApplicationListener(new ClosedListener());
        context.addApplicationListener(new RefreshListener());
        
        // 启动 `Spring应用上下文`
        context.refresh();

        /**
         * output:
         *
         * 603  ms [INFO ] [main            ] SprintEventListenerDemo:18 
         * - 收到事件 org.springframework.context.event.ContextRefreshedEvent
         *          [source=org.springframework.context.support.GenericApplicationContext@67b467e9: startup date [Sat Jul 28 19:56:13 CST 2018]; root of context hierarchy]
         *
         * 648  ms [INFO ] [main            ] SprintEventListenerDemo:18 
         * - 收到事件 org.springframework.context.PayloadApplicationEvent
         *          [source=org.springframework.context.support.GenericApplicationContext@67b467e9: startup date [Sat Jul 28 19:56:13 CST 2018]; root of context hierarchy]
         *
         * 为什么会有两个事件？ ContextRefreshedEvent + PayloadApplicationEvent？
         * --> 
         * /
        // `Spring应用上下文` 发布事件
        context.publishEvent("HelloWorld");

        /**
         * 531  ms [INFO ] [main            ] SprintEventListenerDemo:19 
         * - 收到事件 com.alphawang.distributed.springboot.event.SprintEventListenerDemo$MyEven
         * [source=My Event Arg.]
         * 
         */
        context.publishEvent(new MyEvent("My Event Arg."));

        /**
         * 532  ms [INFO ] [main            ] SprintEventListenerDemo:19 
         * - 收到事件 org.springframework.context.event.ContextClosedEvent
         * [source=org.springframework.context.support.GenericApplicationContext@67b467e9: startup date [Sat Jul 28 19:59:35 CST 2018]; root of context hierarchy]
         *
         */
        context.close();
        
    }
    
    private static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }
    
    private static class ClosedListener implements ApplicationListener<ContextClosedEvent> {

        @Override 
        public void onApplicationEvent(ContextClosedEvent event) {
            log.warn("关闭上下文 {}", event);
        }
    }

    private static class RefreshListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            log.warn("刷新上下文 {}", event);
        }
    }
    
}
