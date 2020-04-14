package com.ex2i.websocket.chat.repo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import com.ex2i.websocket.chat.room.ChatRoom;

@Repository
public class ChatRepository {

	private int id;
	
	private final Map<String, ChatRoom> chatRoomMap = new HashMap<>();

	public ChatRepository() {
		createRoom("첫 대화방");
	}
	
	public int createRoom(String roomName) {
		ChatRoom room = new ChatRoom();
		room.setId((++id) + "");
		room.setName(roomName);
		room.setSessions(new HashSet<>());
		
		chatRoomMap.put(id + "", room);
		
		return id;
	}
	
	public ChatRoom getChatRoom(String id) {
		return chatRoomMap.get(id);
	}

	public Collection<ChatRoom> getChatRooms() {
		return chatRoomMap.values();
	}
	
	public void remove(WebSocketSession session) {
		Collection<ChatRoom> rooms = getChatRooms();
		rooms.parallelStream().forEach(room -> {
			room.remove(session);
		});
	}
	
}
