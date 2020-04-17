package com.ex2i.websocket.game.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:quiz.properties")
@Scope("prototype")
public class QuizFactory {

	@Autowired
	private Environment env;
	
	private List<String> quizList;

	/**
	 * 퀴즈 초기화
	 */
	@PostConstruct
	public void reset() {
		if ( quizList == null ) {
			quizList = new ArrayList<>();
		}
		quizList.clear();
		
		int idx = 0;
		
		String quiz = null;
		while ( true ) {
			quiz = env.getProperty("quiz" + idx);
			if ( quiz == null ) {
				break;
			}
			quizList.addAll( List.of(quiz.split(",")) );
			idx += 1;
		}
		
		quizList = quizList.stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * 퀴즈 목록에서 quizSize만큼만 추려냄.
	 * @param quizSize
	 */
	public void reset(int quizSize) {
		reset();
		
		if ( quizSize == 0 ) {
			quizSize = quizList.size();
		}
		
		List<String> tempQuizList = new ArrayList<>();
		
		if ( quizList.size() > quizSize ) {
			while ( tempQuizList.size() < quizSize ) {
				tempQuizList.add(getQuiz());
			}
			
			quizList.clear();
			quizList.addAll(tempQuizList);
		}
		
	}
	
	/**
	 * 퀴즈 추출
	 * @return
	 */
	public String getQuiz() {
		Random rnd = new Random();
		int idx = rnd.nextInt(quizList.size());
		String quiz = quizList.get(idx);
		quizList.remove(idx);
		return quiz; 
	}
	
	/**
	 * 모든 퀴즈를 다 풀었는지 체크
	 * @return
	 */
	public boolean isEmpty() {
		return quizList.size() == 0;
	}
	
}
