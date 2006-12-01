package org.marketcetera.photon.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.Topic;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConverter;

public class SpringUtils {

	public static void startSpringBean(InitializingBean bean) throws Exception{
		bean.afterPropertiesSet();
	}
	
	public static void stopSpringBean(DisposableBean bean) throws Exception {
		bean.destroy();
	}

	public static void startSpringBean(Lifecycle lifecycleBean)
	{
		lifecycleBean.start();
	}

	public static void stopSpringBean(Lifecycle lifecycleBean){
		lifecycleBean.stop();
	}
	
	public static JmsTemplate createJmsTemplate(ConnectionFactory connectionFactory, Topic topic) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setDefaultDestination(topic);
		jmsTemplate.afterPropertiesSet();
		return jmsTemplate;
	}

	public static MessageListenerAdapter createMessageListenerAdapter(
			String methodName, MessageConverter converter) {
		MessageListenerAdapter listener;
		listener = new MessageListenerAdapter();
		listener.setDefaultListenerMethod(methodName);
		listener.setMessageConverter(converter);
		return listener;
	}

	public static SimpleMessageListenerContainer createSimpleMessageListenerContainer(ConnectionFactory connectionFactory, Object listener, Destination destination, ExceptionListener exceptionListener){
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setMessageListener(listener);
		container.setDestination(destination);
		container.setExceptionListener(exceptionListener);
		container.afterPropertiesSet();
		container.start();
		return container;
	}

}
