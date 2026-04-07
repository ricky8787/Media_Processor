package com.practice.media_processor.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 啟用 STOMP WebSocket 廣播功能
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 設定前端連線的 WebSocket 端點為 /ws，並允許所有跨域請求 (測試方便)
        // withSockJS() 是為了相容不支援 WebSocket 的老舊瀏覽器
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 啟用一個簡單的訊息代理 (Broker)。發送到 /topic 開頭的訊息會被廣播給訂閱的客戶端
        registry.enableSimpleBroker("/topic");
        
        // 客戶端如果要發訊息給伺服器，路徑必須加上 /app 前綴 (不過我們現在只需要單向推播，這行是防呆)
        registry.setApplicationDestinationPrefixes("/app");
    }
}
