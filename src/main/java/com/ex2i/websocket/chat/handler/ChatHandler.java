package com.ex2i.websocket.chat.handler;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.chat.room.ChatRoom;
import com.ex2i.websocket.chat.room.Gamer;
import com.ex2i.websocket.util.SendUtil;

public class ChatHandler {

	private SendUtil sendMessage;
	
	private List<Gamer> sessions;
	
	public ChatHandler(List<Gamer> sessions) {
		this.sessions = sessions;
		sendMessage = new SendUtil(this.sessions);
	}
	
	public void join(WebSocketSession session, ChatMessage chatMessage) {
		sendMessage.joinInRoom(session, chatMessage.getWriter());
		chatMessage.setMessage(chatMessage.getWriter() + "님이 입장하셨습니다.");
		sendMessage.sendMessageToRoomUsers(session, chatMessage);
		sendMessage.sendGamerInfoToRoomUsers(chatMessage.getChatRoomId());
	}
	
	public void chat(ChatRoom room, WebSocketSession session, ChatMessage chatMessage) {
		/*
		 * 정답을 맞췄을 경우
		 */
		if ( room.isStart() ) {
			String quiz = room.getQuiz();
			String message = chatMessage.getMessage().trim();
			
			if ( quiz.equalsIgnoreCase(message) ) {
				chatMessage.setMessageType(MessageType.PASS);
				
				Gamer passGamer = sessions.parallelStream()
											.filter(gamer -> gamer.getSession() == session)
											.findFirst()
											.orElse(null);
				if ( passGamer != null ) {
					passGamer.addScore();
				}
				
				sendMessage.sendGamerInfoToRoomUsers(chatMessage.getChatRoomId());
			}
		}
		sendMessage.sendMessageToRoomUsers(session, chatMessage);
		
	}

	public void secretChat(WebSocketSession session, ChatMessage chatMessage) {
		sendMessage.sendMessageToUser(session, chatMessage);
	}

	public void quit(WebSocketSession session, ChatMessage chatMessage) {
		sendMessage.sendMessageToRoomUsers(session, chatMessage);
	}
	
}
