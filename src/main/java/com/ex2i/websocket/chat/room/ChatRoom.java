package com.ex2i.websocket.chat.room;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.contants.MessageType;
import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.game.GameManager;
import com.ex2i.websocket.game.constants.CommandType;
import com.ex2i.websocket.game.message.GameMessage;
import com.ex2i.websocket.game.message.GamerMessage;
import com.google.gson.Gson;

public class ChatRoom {

	private String id;
	private String name;
	private List<Gamer> sessions;

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
	
	public void setSessions(List<Gamer> sessions) {
		this.sessions = sessions;
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
		if ( chatMessage.getMessageType().equals(MessageType.JOIN) ) {
			joinInRoom(session, chatMessage);
			chatMessage.setMessage(chatMessage.getWriter() + "님이 입장하셨습니다.");
			sendMessageToRoomUsers(session, chatMessage);
			sendGamerInfoToRoomUsers();
		}
		else if ( chatMessage.getMessageType().equals(MessageType.CHAT) ) {
			
			/*
			 * 정답을 맞췄을 경우
			 */
			if ( GameManager.isStart() ) {
				String quiz = GameManager.getQuiz();
				String message = chatMessage.getMessage().trim();
				
				if ( quiz.equalsIgnoreCase(message) ) {
					chatMessage.setMessageType(MessageType.PASS);
					sendGamerInfoToRoomUsers();
				}
			}
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
		else if ( chatMessage.getMessageType().equals(MessageType.QUIT) ) {
			getSessions().parallelStream()
						.forEach(sess -> {
							send(sess, chatMessage);
						});
			sendGamerInfoToRoomUsers();
			
		}
		
	}
	
	/**
	 * 방 내 참여자로 등록
	 * @param session
	 */
	private void joinInRoom(WebSocketSession session, ChatMessage chatMessage) {
		Gamer gamer = new Gamer();
		gamer.setName(chatMessage.getWriter());
		gamer.setScore(0);
		gamer.setSession(session);
		sessions.add(gamer);
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
		getSessions().parallelStream()
				.filter(sess-> sess != session)
				.forEach(sess -> {
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
		getSessions().parallelStream()
				.filter(sess-> sess.getId().equals(toSessionId))
				.forEach(sess -> {
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
			if ( session.isOpen() )
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
		
		Gson gson = new Gson();
		TextMessage textMessage = new TextMessage(gson.toJson(message));
		
		getSessions().parallelStream()
				.forEach(session -> {
					try {
						if ( session.isOpen() )
							session.sendMessage(textMessage);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

	/**
	 * 게임을 종료한다.
	 */
	public void endGame() {
		
		GameMessage message = new GameMessage();
		message.setChatRoomId(id);
		message.setMessageType(MessageType.GAME);
		message.setCommand(CommandType.END);
		
		Gson gson = new Gson();
		TextMessage textMessage = new TextMessage(gson.toJson(message));
		
		getSessions().parallelStream()
				.forEach(session -> {
					try {
						if ( session.isOpen() )
							session.sendMessage(textMessage);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		
	}

	/**
	 * 다음 출제자에게 문제를 제출한다.
	 * @param gamerIdx 다음 출제자 아이디
	 * @param limitTime 게임 제한 시간
	 * @param quiz 문제
	 */
	public void nextTurn(int gamerIdx, int limitTime, String quiz) {
		
		GameMessage message = new GameMessage();
		message.setChatRoomId(id);
		message.setMessageType(MessageType.GAME);
		message.setCommand(CommandType.NEXT_TURN);
		message.setTimer(limitTime);
		message.setQuiz(quiz);
		
		Gson gson = new Gson();
		TextMessage textMessage = new TextMessage(gson.toJson(message));
		
		Set<WebSocketSession> gamerList = getSessions();
		
		int idx = 0;
		for ( WebSocketSession gamer : gamerList ) {
			if ( idx == gamerIdx ) {
				// 출제자에게 문제 및 제어권한 부여
				try {
					if ( gamer.isOpen() )
						gamer.sendMessage(textMessage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// 그 외 참여자의 제어권한 회수
				getSessions().parallelStream()
						.filter(session -> session != gamer)
						.forEach(sess -> {
					GameMessage gameMessage = new GameMessage();
					gameMessage.setChatRoomId(id);
					gameMessage.setMessageType(MessageType.GAME);
					gameMessage.setCommand(CommandType.NOT_MY_TURN);
					gameMessage.setTimer(limitTime);
					TextMessage gameCommandMessage = new TextMessage(gson.toJson(gameMessage));
					
					try {
						if ( sess.isOpen() )
							sess.sendMessage(gameCommandMessage);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				return;
			}
			
			idx += 1;
		}
		
	}
	
	public void sendGamerInfoToRoomUsers() {
		
		List<Gamer> gamerInfoList = getGamers();
		GamerMessage message = new GamerMessage();
		message.setGamer(gamerInfoList);
		message.setChatRoomId(id);
		
		Gson gson = new Gson();
		String json = gson.toJson(message);
		TextMessage textMessage = new TextMessage(json);
		
		getSessions().parallelStream()
					.forEach(session -> {
						try {
							if ( session.isOpen() )
								session.sendMessage(textMessage);
						} catch (IOException e) {
							e.printStackTrace();
						} 
					});
		
	}

}
