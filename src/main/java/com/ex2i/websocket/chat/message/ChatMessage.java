package com.ex2i.websocket.chat.message;

import java.util.Map;

public class ChatMessage {

	private String chatRoomId;
	
	/**
	 * 세션아이디
	 */
	private String sessionId;

	/**
	 * 이름
	 */
	private String writer;
	private String message;
	private Map<String, String> point;
	private String messageType;
	private String toSessionId;
	private String fromMe;

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getPoint() {
		return point;
	}

	public void setPoint(Map<String, String> point) {
		this.point = point;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public boolean isMessageType( String ... messageTypes) {
		
		for (String messageType : messageTypes) {
			if ( this.messageType.equalsIgnoreCase(messageType) ) {
				return true;
			}
		}
		
		return false;
	}

	public String getToSessionId() {
		return toSessionId;
	}

	public void setToSessionId(String toSessionId) {
		this.toSessionId = toSessionId;
	}

	public String getFromMe() {
		return fromMe;
	}

	public void setFromMe(String fromMe) {
		this.fromMe = fromMe;
	}

}
