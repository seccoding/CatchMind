package com.ex2i.websocket.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class GetBean implements ApplicationContextAware {

	private static ApplicationContext ctx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String beanName) {
		if ( ctx != null ) {
			return (T) ctx.getBean(beanName);
		}
		
		return null;
	}
	
}
