package com.ex2i.websocket.chat.room;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

public class Gamer {

	private transient WebSocketSession session;
	private String sessionId;
	private String name;
	private int score;

	public WebSocketSession getSession() {
		return session;
	}

	public void setSession(WebSocketSession session) {
		this.session = session;
		this.sessionId = session.getId();
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void send(Object message) {
		Gson gson = new Gson();
		
		TextMessage textMessage = new TextMessage(gson.toJson(message));
		try {
			if ( session.isOpen() )
				session.sendMessage(textMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addScore() {
		this.score += 1;
	}
	
}
