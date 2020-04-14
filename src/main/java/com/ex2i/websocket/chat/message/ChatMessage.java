package com.ex2i.websocket.chat.message;

import java.util.Map;

public class ChatMessage {

	private String chatRoomId;
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

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Map<String, String> getPoint() {
		return point;
	}

	public void setPoint(Map<String, String> point) {
		this.point = point;
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
