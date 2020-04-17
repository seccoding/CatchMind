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
import com.ex2i.websocket.chat.room.ChatRoom;
import com.ex2i.websocket.game.constants.CommandType;
import com.ex2i.websocket.game.message.GameMessage;
import com.google.gson.Gson;

@Component
public class SessionHandler extends TextWebSocketHandler {

	@Autowired
	private ChatRepository repository;
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		String payload = message.getPayload();
		payload = payload.replace("<", "&lt;").replace(">", "&gt;");
		Gson gson = new Gson();
		
		ChatMessage chatMessage = gson.fromJson(payload, ChatMessage.class);
		chatMessage.setSessionId(session.getId());
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
			ChatRoom room = repository.getChatRoom(chatMessage.getChatRoomId());
			
			GameMessage gameMessage = gson.fromJson(payload, GameMessage.class);
			room.gameHandle(gameMessage);
		}
		/*
		 * 자신이 속한 방에만 전송
		 */
		else {
			ChatRoom room = repository.getChatRoom(chatMessage.getChatRoomId());
			room.handle(session, chatMessage);
			
			// 정답을 맞추었을 때
			if ( chatMessage.getMessageType().equals(MessageType.PASS) ) {
				GameMessage gameMessage = new GameMessage();
				gameMessage.setChatRoomId(chatMessage.getChatRoomId());
				gameMessage.setCommand(CommandType.NEXT_TURN);
				
				room.gameHandle(gameMessage);
			}
			
		}
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		String roomId = repository.getRoomId(session);
		
		if ( roomId != null ) {
			repository.remove(roomId, session);
			
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setSessionId(session.getId());
			chatMessage.setMessageType(MessageType.QUIT);
			chatMessage.setMessage("님이 게임에서 나갔습니다.");
			
			repository.getChatRoom(roomId).handle(null, chatMessage);
		}
	}
	
}
