package com.ex2i.websocket.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ex2i.websocket.chat.repo.ChatRepository;
import com.ex2i.websocket.chat.room.ChatRoom;

@Component
public class GameManager {

	@Autowired
	private ChatRepository repo;
	
	@Autowired
	private Quiz quiz;
	
	private int idx;
	
	public void startGame(String roomId) {
		idx = 0;
		ChatRoom room = repo.getChatRoom(roomId);
		room.startGame();
	}
	
	public void endGame(String roomId) {
		ChatRoom room = repo.getChatRoom(roomId);
		room.endGame();
	}
	
	public void nextTurn(String roomId) {
		
		if ( quiz.isEmpty() ) {
			endGame(roomId);
			return;
		}
		
		ChatRoom room = repo.getChatRoom(roomId);
		
		if ( room.getSessions().size() == idx ) {
			idx = 0;
		}
		
		room.nextTurn(idx, 180, quiz.getQuiz());
		idx += 1;
		
	}
	
}
