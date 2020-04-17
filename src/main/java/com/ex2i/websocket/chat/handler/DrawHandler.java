package com.ex2i.websocket.chat.handler;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.chat.room.Gamer;

public class DrawHandler {

	private SendMessage sendMessage;
	
	public DrawHandler(List<Gamer> sessions) {
		sendMessage = new SendMessage(sessions);
	}
	
	public void draw(WebSocketSession session, ChatMessage chatMessage) {
		sendMessage.sendMessageToRoomUsersWithoutMe(session, chatMessage);
	}
	
}
