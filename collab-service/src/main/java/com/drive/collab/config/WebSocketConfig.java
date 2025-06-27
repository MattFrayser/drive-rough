package com.drive.collab.config;

import com.drive.collab.security.JwtWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtWebSocketInterceptor jwtWebSocketInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/collab")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtWebSocketInterceptor)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for broadcasting messages
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix for client-to-server messages
        registry.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }
}
