package com.ex2i.websocket.chat.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.chat.repo.ChatRepository;
import com.ex2i.websocket.game.GameManager;
import com.ex2i.websocket.game.GameMessage;
import com.google.gson.Gson;

@Component
public class ChatHandler extends TextWebSocketHandler {

	@Autowired
	private GameManager gameManager;
	
	@Autowired
	private ChatRepository repository;
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		String payload = message.getPayload();
		payload = payload.replace("<", "&lt;").replace(">", "&gt;");
		Gson gson = new Gson();
		
		ChatMessage chatMessage = gson.fromJson(payload, ChatMessage.class);
		
		/*
		 * 모든 채팅방에 전송
		 */
		if ( chatMessage.getMessageType().equals(MessageType.ALL) ) {
			repository.getChatRooms().parallelStream().forEach(room -> {
				room.handle(session, chatMessage);
			});
		}
		/*
		 * 게임 Rule 관리
		 */
		else if ( chatMessage.getMessageType().equals(MessageType.GAME) ) {
			GameMessage gameMessage = gson.fromJson(payload, GameMessage.class);
			gameManager.handle(gameMessage);
		}
		/*
		 * 자신이 속한 방에만 전송
		 */
		else {
			repository.getChatRoom(chatMessage.getChatRoomId()).handle(session, chatMessage);
		}
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		repository.remove(session);
	}
	
}
