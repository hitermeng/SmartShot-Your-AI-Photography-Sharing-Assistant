package cn.aicamera.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 与模型端交互的client
 */
@Configuration
public class ClientConfig {
    @Bean
    public RestTemplate restTemplate (){
        return new RestTemplate();
    }
}
