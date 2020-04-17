package com.ex2i.websocket.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.message.ChatMessage;
import com.ex2i.websocket.chat.room.Gamer;
import com.ex2i.websocket.game.message.GamerMessage;

public class SendUtil {

	private List<Gamer> sessions;
	
	public SendUtil(List<Gamer> sessions) {
		this.sessions = sessions;
	}
	
	/**
	 * 방 내 참여자로 등록
	 * @param session
	 */
	public void joinInRoom(WebSocketSession session, String writer) {
		Gamer gamer = new Gamer();
		gamer.setName(writer);
		gamer.setScore(0);
		gamer.setSession(session);
		sessions.add(gamer);
	}
	
	/**
	 * 방 내의 모든 참여자에게 전송
	 * 자신, 그외의 메시지 타입이 다름.
	 * @param session
	 * @param chatMessage
	 */
	public void sendMessageToRoomUsers(WebSocketSession session, Object message) {
		sendToMe(session, message);
		sendMessageToRoomUsersWithoutMe(session, message);
	}
	
	/**
	 * 나에게 전송
	 * @param session
	 * @param chatMessage
	 */
	public void sendToMe(WebSocketSession session, Object message) {
		if ( message instanceof ChatMessage ) {
			((ChatMessage) message).setFromMe("me");
		}
		
		sessions.parallelStream()
				.filter(gamer -> gamer.getSession() == session)
				.filter(gamer -> gamer.getSession().isOpen())
				.forEach(gamer -> {
					gamer.send(message);
				});

		if ( message instanceof ChatMessage ) {
			((ChatMessage) message).setFromMe("other");
		}
	}
	
	/**
	 * 방 내에 나를 제외한 모든 참여자에게 전송
	 * @param session
	 * @param chatMessage
	 */
	public void sendMessageToRoomUsersWithoutMe(WebSocketSession session, Object message) {
		sessions.parallelStream()
				.filter(gamer -> gamer.getSession() != session)
				.filter(gamer -> gamer.getSession().isOpen())
				.forEach(gamer -> {
					gamer.send(message);
				});
	}
	
	/**
	 * 특정 유저에게 전송
	 * @param session
	 * @param chatMessage
	 */
	public void sendMessageToUser(WebSocketSession session, ChatMessage chatMessage) {
		sendToMe(session, chatMessage);
		String toSessionId = chatMessage.getToSessionId();
		sessions.parallelStream()
				.filter(gamer -> gamer.getSession().getId().equals(toSessionId))
				.filter(gamer -> gamer.getSession().isOpen())
				.forEach(gamer -> {
					gamer.send(chatMessage);
				});
	}
	
	/**
	 * 방내 참여자 정보 전송.
	 */
	public void sendGamerInfoToRoomUsers(String roomId) {
		
		GamerMessage message = new GamerMessage();
		message.setGamer( sessions.parallelStream().filter(gamer -> gamer.getSession().isOpen()).collect(Collectors.toList()) );
		message.setChatRoomId(roomId);
		
		sessions.parallelStream()
				.filter(gamer -> gamer.getSession().isOpen())
				.forEach(gamer -> {
					gamer.send(message);
				});
	}
	
	/**
	 * 방 내 모든 참여자에게 발송.
	 * @param message
	 */
	public void sendToAllInRoom(Object message) {
		sessions.parallelStream()
				.filter(gamer -> gamer.getSession().isOpen())
				.forEach(gamer -> {
					gamer.send(message);
				});
	}
	
}
