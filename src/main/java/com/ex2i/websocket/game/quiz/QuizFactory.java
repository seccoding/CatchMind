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
