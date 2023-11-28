package com.ex2i.websocket.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.repo.ChatRepository;
import com.ex2i.websocket.chat.room.ChatRoom;
import com.ex2i.websocket.game.constants.CommandType;
import com.ex2i.websocket.game.message.GameMessage;
import com.ex2i.websocket.game.quiz.QuizFactory;
import com.ex2i.websocket.util.GetBean;

@Component
@Scope("prototype")
public class GameManager {

	@Autowired
	private ChatRepository repo;
	
	private int count = 10;
	
	private QuizFactory quizFactory;
	
	private boolean isStart;
	
	private String quiz;
	
	private int idx;
	
	public boolean isStart() {
		return isStart;
	}
	
	public String getQuiz() {
		return quiz;
	}
	
	/**   
	 * 게임 Rule을 관리한다.
	 * @param gameMessage
	 */
	public void handle(GameMessage gameMessage) {
		
		if ( quizFactory == null ) {
			quizFactory = GetBean.get("quizFactory");
		}
		
		String command = gameMessage.getCommand();
		String roomId = gameMessage.getChatRoomId();
		
		if ( command.equals(CommandType.START) ) {
			startGame(roomId, gameMessage.getQuizSize());
		}
		else if ( command.equals(CommandType.END) ) {
			endGame(roomId);
		}
		else if ( command.equals(CommandType.NEXT_TURN) ) {
			nextTurn(roomId);
		}
		
	}
	
	/**
	 * 게임시작 숨김/표시 처리
	 * @param roomId
	 */
	public void showOrHideStartButton(String roomId) {
		ChatRoom room = repo.getChatRoom(roomId);
		
		if ( room == null ) {
			return;
		}
		
		if ( room.isStart() ) {
			GameMessage message = new GameMessage();
			message.setChatRoomId(roomId);
			message.setMessageType(MessageType.GAME);
			message.setCommand(CommandType.HIDE_START_BTN);
			room.getGamers().parallelStream().forEach(gamer -> {
				gamer.send(message);
			});
			return;
		}
		
		int gamerCnt = room.getGamers().size();
		GameMessage message = new GameMessage();
		message.setChatRoomId(roomId);
		message.setMessageType(MessageType.GAME);
		
		// 첫번째 플레이어에게 게임시작 버튼 노출.
		if ( gamerCnt > 1 ) {
			message.setCommand(CommandType.SHOW_START_BTN);
			room.getGamers().get(0).send(message);
		}
		// 모든 플레이어에게 게임시작 버튼 숨김.
		else {
			message.setCommand(CommandType.HIDE_START_BTN);
			room.getGamers().parallelStream().forEach(gamer -> {
				gamer.send(message);
			});
		}
	}
	
	/**
	 * 게임을 시작한다.
	 * @param roomId
	 */
	private void startGame(String roomId, int quizSize) {
		isStart = true;
		idx = 0;
		quizFactory.reset(quizSize);
		ChatRoom room = repo.getChatRoom(roomId);
		room.startGame();
		nextTurn(roomId);
	}
	
	/**
	 * 게임을 종료한다.
	 * @param roomId
	 */
	public void endGame(String roomId) {
		isStart = false;
		ChatRoom room = repo.getChatRoom(roomId);
		room.endGame();
	}
	
	/**
	 * 다음 출제자에게 문제를 넘긴다.
	 * @param roomId
	 */
	private void nextTurn(String roomId) {
		
		if ( quizFactory.isEmpty() || !isStart ) {
			endGame(roomId);
			return;
		}
		
		ChatRoom room = repo.getChatRoom(roomId);
		
		boolean isGameOver = room.getGamers().parallelStream()
								.filter(gamer -> gamer.getSession().isOpen())
								.filter(gamer -> gamer.getScore() >= count)
								.count() > 0;

		if (isGameOver) {
			endGame(roomId);
			return;
		}
		
		isStart = true;
		
		if ( room.getSessions().size() == idx ) {
			idx = 0;
		}
		
		quiz = quizFactory.getQuiz();
		
		room.nextTurn(idx, 180, quiz);
		idx += 1;
		
	}
	
}
