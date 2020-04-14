package com.ex2i.websocket.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ex2i.websocket.chat.repo.ChatRepository;

@Controller
public class ChatController {

	@Autowired
	private ChatRepository repo;
	
	@GetMapping("/room")
	public ModelAndView viewChatRoomList() {
		ModelAndView view = new ModelAndView("roomlist");
		view.addObject("rooms", repo.getChatRooms());
		
		return view;
	}
	
	
	@PostMapping("/room")
	@ResponseBody
	public int createChatRoom(@RequestParam String roomName) {
		roomName = roomName.replace("<", "&lt;").replace(">", "&gt;");
		int roomNumber = repo.createRoom(roomName);
		return roomNumber;
	}
	
	@GetMapping("/room/{id:[0-9]+}")
	public ModelAndView viewChatRoom(
			@PathVariable String id,
			@RequestParam String userName
		) {
		userName = userName.replace("<", "&lt;").replace(">", "&gt;");
		ModelAndView view = new ModelAndView("room");	
		view.addObject("room", repo.getChatRoom(id));
		view.addObject("userName", userName);
		return view;
	}
	
}
