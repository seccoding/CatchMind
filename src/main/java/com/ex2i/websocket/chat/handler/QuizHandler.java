package com.ex2i.websocket.chat.handler;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.room.Gamer;
import com.ex2i.websocket.game.constants.CommandType;
import com.ex2i.websocket.game.message.GameMessage;
import com.ex2i.websocket.util.SendUtil;

public class QuizHandler {

	private SendUtil sendMessage;
	
	private List<Gamer> sessions;
	
	public QuizHandler(List<Gamer> sessions) {
		sendMessage = new SendUtil(sessions);
		this.sessions = sessions;
	}
	
	public void nextTurn(GameMessage gameMessage, int gamerIdx) {
		
		WebSocketSession gamer = this.sessions.get(gamerIdx).getSession();
		gameMessage.setPlayerSessionId(gamer.getId());
		
		// 출제자에게 문제 및 제어권한 부여
		sendMessage.sendToMe(gamer, gameMessage);
		
		// 그 외 참여자의 제어권한 회수
		gameMessage.setCommand(CommandType.NOT_MY_TURN);
		gameMessage.setQuiz(null);
		
		sendMessage.sendMessageToRoomUsersWithoutMe(gamer, gameMessage);
		
	}
	
}
