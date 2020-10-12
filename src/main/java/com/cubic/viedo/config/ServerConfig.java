package com.cubic.viedo.config;


import com.cubic.viedo.webscoket.MatrixNettyWebServer;
import com.cubic.viedo.webscoket.WebRequestHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ServerConfig
 * @Author QIANGLU
 * @Date 2020/3/19 3:31 下午
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(value = ServerProperties.class)
public class ServerConfig {



    @Bean(initMethod = "start")
    public MatrixNettyWebServer matrixNettyWebServer(ServerProperties serverProperties, WebRequestHandler webRequestHandler, ApplicationEventPublisher applicationEventPublisher) {
        return new MatrixNettyWebServer(serverProperties.getAgentPort(), webRequestHandler,applicationEventPublisher);
    }
}
