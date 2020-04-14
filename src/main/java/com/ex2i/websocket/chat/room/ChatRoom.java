package com.ex2i.websocket.chat.room;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.message.ChatMessage;
import com.google.gson.Gson;

public class ChatRoom {

	private String id;
	private String name;
	private Set<WebSocketSession> sessions = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<WebSocketSession> getSessions() {
		return sessions;
	}

	public void setSessions(Set<WebSocketSession> sessions) {
		this.sessions = sessions;
	}
	
	public void handle(WebSocketSession session, ChatMessage chatMessage) {
		if ( chatMessage.getMessageType().equals(MessageType.JOIN) ) {
			joinInRoom(session);
			chatMessage.setMessage(chatMessage.getWriter() + "님이 입장하셨습니다.");
			sendMessageToRoomUsers(session, chatMessage);
		}
		else if ( chatMessage.getMessageType().equals(MessageType.CHAT) ) {
			sendMessageToRoomUsers(session, chatMessage);
		}
		else if ( chatMessage.getMessageType().equals(MessageType.DRAW)
				|| chatMessage.getMessageType().equals(MessageType.START_DRAW) 
				|| chatMessage.getMessageType().equals(MessageType.CLEAR_CANVAS) ) {
			sendMessageToRoomUsersWithoutMe(session, chatMessage);
		}
		else if ( chatMessage.getMessageType().equals(MessageType.SECRET) ) {
			sendMessageToUser(session, chatMessage);
		}
	}
	
	/**
	 * 방 내 참여자로 등록
	 * @param session
	 */
	private void joinInRoom(WebSocketSession session) {
		sessions.add(session);
	}
	
	/**
	 * 방 내의 모든 참여자에게 전송
	 * @param session
	 * @param chatMessage
	 */
	private void sendMessageToRoomUsers(WebSocketSession session, ChatMessage chatMessage) {
		sendToMe(session, chatMessage);
		sendMessageToRoomUsersWithoutMe(session, chatMessage);
	}
	
	/**
	 * 방 내에 나를 제외한 모든 참여자에게 전송
	 * @param session
	 * @param chatMessage
	 */
	private void sendMessageToRoomUsersWithoutMe(WebSocketSession session, ChatMessage chatMessage) {
		sessions.parallelStream().filter(sess-> sess != session).forEach(sess -> {
			send(sess, chatMessage);
		});
	}
	
	/**
	 * 특정 유저에게 전송
	 * @param session
	 * @param chatMessage
	 */
	private void sendMessageToUser(WebSocketSession session, ChatMessage chatMessage) {
		sendToMe(session, chatMessage);
		String toSessionId = chatMessage.getToSessionId();
		sessions.parallelStream().filter(sess-> sess.getId().equals(toSessionId)).forEach(sess -> {
			send(sess, chatMessage);
		});
	}
	
	/**
	 * 나에게 전송
	 * @param session
	 * @param chatMessage
	 */
	private void sendToMe(WebSocketSession session, ChatMessage chatMessage) {
		chatMessage.setFromMe("me");
		send(session, chatMessage);
		
		chatMessage.setFromMe("other");
	}
	
	/**
	 * 전송 공통
	 * @param session
	 * @param chatMessage
	 */
	private void send(WebSocketSession session, ChatMessage chatMessage) {
		Gson gson = new Gson();
		
		TextMessage textMessage = new TextMessage(gson.toJson(chatMessage));
		try {
			session.sendMessage(textMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 방을 나간 참여자 제거
	 * @param session
	 */
	public void remove(WebSocketSession session) {
		sessions.remove(session);
	}

}
