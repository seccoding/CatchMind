package com.ex2i.websocket.game.message;

public class GameMessage {

	private String messageType;
	private String chatRoomId;
	private String command;
	private int timer;
	private String quiz;
	private int quizSize;
	private String playerSessionId;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public String getQuiz() {
		return quiz;
	}

	public void setQuiz(String quiz) {
		this.quiz = quiz;
	}

	public int getQuizSize() {
		return quizSize;
	}

	public void setQuizSize(int quizSize) {
		this.quizSize = quizSize;
	}

	public String getPlayerSessionId() {
		return playerSessionId;
	}

	public void setPlayerSessionId(String playerSessionId) {
		this.playerSessionId = playerSessionId;
	}

}
