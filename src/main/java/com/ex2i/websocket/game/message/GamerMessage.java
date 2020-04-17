package com.ex2i.websocket.game.message;

import java.util.List;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.room.Gamer;

public class GamerMessage {

	private String messageType = MessageType.GAMER_INFO;
	private String chatRoomId;
	private List<Gamer> gamer;

	public String getMessageType() {
		return messageType;
	}

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public List<Gamer> getGamer() {
		return gamer;
	}

	public void setGamer(List<Gamer> gamer) {
		this.gamer = gamer;
	}

}
