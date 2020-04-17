package com.ex2i.websocket.chat.room;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.handler.ChatHandler;
import com.ex2i.websocket.chat.handler.DrawHandler;
import com.ex2i.websocket.chat.handler.QuizHandler;
import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.game.GameManager;
import com.ex2i.websocket.game.constants.CommandType;
import com.ex2i.websocket.game.message.GameMessage;
import com.ex2i.websocket.util.GetBean;
import com.ex2i.websocket.util.SendUtil;

public class ChatRoom {

	private String id;
	private String name;
	private List<Gamer> sessions;

	private SendUtil sendMessage;
	private ChatHandler chatHandler;
	private DrawHandler drawHandler;
	private QuizHandler quizHandler;
	
	private GameManager gameManager;
	
	public ChatRoom(List<Gamer> sessions) {
		this.sessions = sessions;
		sendMessage = new SendUtil(this.sessions);
		chatHandler = new ChatHandler(this.sessions);
		drawHandler = new DrawHandler(this.sessions);
		quizHandler = new QuizHandler(this.sessions);
	}
	
	private void initGameManager() {
		if ( gameManager == null ) {
			gameManager = GetBean.get("gameManager");
		}
	}
	
	public boolean isStart() {
		initGameManager();
		return gameManager.isStart();
	}
	
	public String getQuiz() {
		initGameManager();
		return gameManager.getQuiz();
	}
	
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
	
	public Set<WebSocketSession> getClosedSessions() {
		return sessions.parallelStream()
						.filter(gamer -> !gamer.getSession().isOpen())
						.map(gamer -> gamer.getSession())
						.collect(Collectors.toSet());
	}
	
	public Set<WebSocketSession> getSessions() {
		return sessions.parallelStream()
						.filter(gamer -> gamer.getSession().isOpen())
						.map(gamer -> gamer.getSession())
						.collect(Collectors.toSet());
	}
	
	public List<Gamer> getGamers() {
		return sessions.stream().filter(gamer -> gamer.getSession().isOpen()).collect(Collectors.toList());
	}

	
	/**
	 * 채팅 및 그림 그리기 전송
	 * @param session
	 * @param chatMessage
	 */
	public void handle(WebSocketSession session, ChatMessage chatMessage) {
		
		if ( chatMessage.isMessageType(MessageType.JOIN) ) {
			chatHandler.join(session, chatMessage);
			
			initGameManager();
			gameManager.showOrHideStartButton(chatMessage.getChatRoomId());
		}
		else if ( chatMessage.isMessageType(MessageType.CHAT) ) {
			chatHandler.chat(this, session, chatMessage);
		}
		else if ( chatMessage.isMessageType(MessageType.DRAW, MessageType.START_DRAW, MessageType.CLEAR_CANVAS) ) {
			drawHandler.draw(session, chatMessage);
		}
		else if ( chatMessage.isMessageType(MessageType.SECRET) ) {
			chatHandler.secretChat(session, chatMessage);
		}
		else if ( chatMessage.isMessageType(MessageType.QUIT) ) {
			chatHandler.quit(session, chatMessage);
			sendMessage.sendGamerInfoToRoomUsers(chatMessage.getChatRoomId());
			
			initGameManager();
			gameManager.showOrHideStartButton(chatMessage.getChatRoomId());
		}
		else if ( chatMessage.isMessageType(MessageType.ALL) ) {
			sendMessage.sendToAllInRoom(chatMessage);
		}
		
	}
	
	public void gameHandle(GameMessage gameMessage) {
		initGameManager();
		gameManager.handle(gameMessage);
	}
	
	
	/**
	 * 방을 나간 참여자 제거
	 * @param session
	 */
	public void remove(WebSocketSession session) {
		
		Gamer quitGamer = sessions.parallelStream().filter(gamer -> gamer.getSession() == session).findFirst().orElse(null);
		
		if ( quitGamer != null ) {
			sessions.remove(quitGamer);
		}
	}

	/**
	 * 게임을 시작한다.
	 */
	public void startGame() {
		
		GameMessage message = new GameMessage();
		message.setChatRoomId(id);
		message.setMessageType(MessageType.GAME);
		message.setCommand(CommandType.START);
		
		sendMessage.sendToAllInRoom(message);
	}

	/**
	 * 게임을 종료한다.
	 */
	public void endGame() {
		
		GameMessage message = new GameMessage();
		message.setChatRoomId(id);
		message.setMessageType(MessageType.GAME);
		message.setCommand(CommandType.END);
		
		sendMessage.sendToAllInRoom(message);
		
	}

	/**
	 * 다음 출제자에게 문제를 제출한다.
	 * @param gamerIdx 다음 출제자 아이디
	 * @param limitTime 게임 제한 시간
	 * @param quiz 문제
	 */
	public void nextTurn(int gamerIdx, int limitTime, String quiz) {
		
		GameMessage gameMessage = new GameMessage();
		gameMessage.setChatRoomId(id);
		gameMessage.setMessageType(MessageType.GAME);
		gameMessage.setCommand(CommandType.NEXT_TURN);
		gameMessage.setTimer(limitTime);
		gameMessage.setQuiz(quiz);
		
		quizHandler.nextTurn(gameMessage, gamerIdx);
		
	}

}
