package com.ex2i.websocket.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public class Quiz {

	private List<String> quizList;

	public Quiz() {
		reset();
	}
	
	public void reset() {
		quizList = new ArrayList<>();
		// 1 ~ 100 까지
		IntStream.rangeClosed(1, 100).forEach(num -> {
			quizList.add("퀴즈 " + num);
		});
	}
	
	public String getQuiz() {
		Random rnd = new Random();
		int idx = rnd.nextInt(quizList.size());
		String quiz = quizList.get(idx);
		quizList.remove(idx);
		return quiz; 
	}
	
	public boolean isEmpty() {
		return quizList.size() == 0;
	}
	
}
