package com.ex2i.websocket.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ex2i.websocket.chat.handler.SessionHandler;

@Configuration
@EnableWebSocket
public class ApplicationConfigure implements WebSocketConfigurer {

	@Autowired
	private SessionHandler handler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(handler, "/myHandler").setAllowedOrigins("*").withSockJS();
	}

}
