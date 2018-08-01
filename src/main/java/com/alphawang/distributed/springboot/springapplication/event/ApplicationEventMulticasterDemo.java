package com.alphawang.distributed.springboot.springapplication.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

@Slf4j
public class ApplicationEventMulticasterDemo {

    public static void main(String[] args) {
        ApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override 
            public void onApplicationEvent(ApplicationEvent event) {
                log.info("收到事件 {}", event);
            }
        });

        /**
         * output:
         * 收到事件 org.springframework.context.PayloadApplicationEvent[source=source1]
         * 收到事件 org.springframework.context.PayloadApplicationEvent[source=source2]
         */
        multicaster.multicastEvent(new PayloadApplicationEvent<Object>("source1", "payload"));
        multicaster.multicastEvent(new PayloadApplicationEvent<Object>("source2", "payload"));
    }
}
