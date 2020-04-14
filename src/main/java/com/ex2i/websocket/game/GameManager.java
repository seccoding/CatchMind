package com.ex2i.websocket.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ex2i.websocket.chat.repo.ChatRepository;
import com.ex2i.websocket.chat.room.ChatRoom;
import com.ex2i.websocket.game.constants.CommandType;

@Component
public class GameManager {

	@Autowired
	private ChatRepository repo;
	
	@Autowired
	private Quiz quiz;
	
	private int idx;
	
	/**
	 * 게임 Rule을 관리한다.
	 * @param gameMessage
	 */
	public void handle(GameMessage gameMessage) {
		String command = gameMessage.getCommand();
		String roomId = gameMessage.getChatRoomId();
		
		if ( command.equals(CommandType.START) ) {
			startGame(roomId);
		}
		else if ( command.equals(CommandType.END) ) {
			endGame(roomId);
		}
		else if ( command.equals(CommandType.NEXT_TURN) ) {
			nextTurn(roomId);
		}
		
	}
	
	/**
	 * 게임을 시작한다.
	 * @param roomId
	 */
	private void startGame(String roomId) {
		idx = 0;
		ChatRoom room = repo.getChatRoom(roomId);
		room.startGame();
		nextTurn(roomId);
	}
	
	/**
	 * 게임을 종료한다.
	 * @param roomId
	 */
	private void endGame(String roomId) {
		ChatRoom room = repo.getChatRoom(roomId);
		room.endGame();
	}
	
	/**
	 * 다음 출제자에게 문제를 넘긴다.
	 * @param roomId
	 */
	private void nextTurn(String roomId) {
		
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
