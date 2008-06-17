package org.marketcetera.photon;

import javax.jms.Destination;
import javax.jms.Message;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.core.SessionCallback;

public class NullJmsOperations implements JmsOperations{

	public void convertAndSend(Object message) throws JmsException {
	}

	public void convertAndSend(Destination destination, Object message) throws JmsException {
	}

	public void convertAndSend(String destinationName, Object message) throws JmsException {
	}

	public void convertAndSend(Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public void convertAndSend(Destination destination, Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public void convertAndSend(String destinationName, Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public Object execute(SessionCallback action) throws JmsException {
		return null;
	}

	public Object execute(ProducerCallback action) throws JmsException {
		return null;
	}

	public Message receive() throws JmsException {
		return null;
	}

	public Message receive(Destination destination) throws JmsException {
		return null;
	}

	public Message receive(String destinationName) throws JmsException {
		return null;
	}

	public Object receiveAndConvert() throws JmsException {
		return null;
	}

	public Object receiveAndConvert(Destination destination) throws JmsException {
		return null;
	}

	public Object receiveAndConvert(String destinationName) throws JmsException {
		return null;
	}

	public Message receiveSelected(String messageSelector) throws JmsException {
		return null;
	}

	public Message receiveSelected(Destination destination, String messageSelector) throws JmsException {
		return null;
	}

	public Message receiveSelected(String destinationName, String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(Destination destination, String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(String destinationName, String messageSelector) throws JmsException {
		return null;
	}

	public void send(MessageCreator messageCreator) throws JmsException {
	}

	public void send(Destination destination, MessageCreator messageCreator) throws JmsException {
	}

	public void send(String destinationName, MessageCreator messageCreator) throws JmsException {
	}

}
