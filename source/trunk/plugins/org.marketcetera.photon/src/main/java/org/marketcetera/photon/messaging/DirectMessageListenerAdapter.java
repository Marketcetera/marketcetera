
package org.marketcetera.photon.messaging;

import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.marketcetera.core.LoggerAdapter;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.Assert;

/**
 * The source of this file is derived from 
 * org.springframework.jms.listener.adapter.MessageListenerAdapter
 * in version 2.0 of the Spring Framework.  It is used here under the 
 * terms of the Apache License, Version 2.0.  The original disclaimer
 * is reproduced here:
 * 
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
public abstract class DirectMessageListenerAdapter implements SessionAwareMessageListener {


	private Object defaultResponseDestination;

	private DestinationResolver destinationResolver = new DynamicDestinationResolver();

	private MessageConverter messageConverter;


	/**
	 * Create a new {@link MessageListenerAdapter} with default settings.
	 */
	public DirectMessageListenerAdapter() {
		initDefaultStrategies();
	}

	/**
	 * Create a new {@link MessageListenerAdapter} for the given delegate.
	 */
	public DirectMessageListenerAdapter(Object delegate) {
		initDefaultStrategies();
	}


	/**
	 * Set the default destination to send response messages to. This will be applied
	 * in case of a request message that does not carry a "JMSReplyTo" field.
	 * <p>Response destinations are only relevant for listener methods that return
	 * result objects, which will be wrapped in a response message and sent to a
	 * response destination.
	 * <p>Alternatively, specify a "defaultResponseQueueName" or "defaultResponseTopicName",
	 * to be dynamically resolved via the DestinationResolver.
	 * @see #setDefaultResponseQueueName(String)
	 * @see #setDefaultResponseTopicName(String)
	 * @see #getResponseDestination
	 */
	public void setDefaultResponseDestination(Destination destination) {
		this.defaultResponseDestination = destination;
	}

	/**
	 * Set the name of the default response queue to send response messages to.
	 * This will be applied in case of a request message that does not carry a
	 * "JMSReplyTo" field.
	 * <p>Alternatively, specify a JMS Destination object as "defaultResponseDestination".
	 * @see #setDestinationResolver
	 * @see #setDefaultResponseDestination(javax.jms.Destination)
	 */
	public void setDefaultResponseQueueName(String destinationName) {
		this.defaultResponseDestination = new DestinationNameHolder(destinationName, false);
	}

	/**
	 * Set the name of the default response topic to send response messages to.
	 * This will be applied in case of a request message that does not carry a
	 * "JMSReplyTo" field.
	 * <p>Alternatively, specify a JMS Destination object as "defaultResponseDestination".
	 * @see #setDestinationResolver
	 * @see #setDefaultResponseDestination(javax.jms.Destination)
	 */
	public void setDefaultResponseTopicName(String destinationName) {
		this.defaultResponseDestination = new DestinationNameHolder(destinationName, true);
	}

	/**
	 * Set the DestinationResolver that should be used to resolve response
	 * destination names for this adapter.
	 * <p>The default resolver is a DynamicDestinationResolver. Specify a
	 * JndiDestinationResolver for resolving destination names as JNDI locations.
	 * @see org.springframework.jms.support.destination.DynamicDestinationResolver
	 * @see org.springframework.jms.support.destination.JndiDestinationResolver
	 */
	public void setDestinationResolver(DestinationResolver destinationResolver) {
		Assert.notNull(destinationResolver, "DestinationResolver must not be null");
		this.destinationResolver = destinationResolver;
	}

	/**
	 * Return the DestinationResolver for this adapter.
	 */
	protected DestinationResolver getDestinationResolver() {
		return destinationResolver;
	}

	/**
	 * Set the converter that will convert incoming JMS messages to
	 * listener method arguments, and objects returned from listener
	 * methods back to JMS messages.
	 * <p>The default converter is a {@link SimpleMessageConverter}, which is able
	 * to handle {@link javax.jms.BytesMessage BytesMessages},
	 * {@link javax.jms.TextMessage TextMessages} and
	 * {@link javax.jms.ObjectMessage ObjectMessages}.
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	/**
	 * Return the converter that will convert incoming JMS messages to
	 * listener method arguments, and objects returned from listener
	 * methods back to JMS messages.
	 */
	protected MessageConverter getMessageConverter() {
		return messageConverter;
	}


	/**
	 * Spring {@link SessionAwareMessageListener} entry point.
	 * <p>Delegates the message to the target listener method, with appropriate
	 * conversion of the message argument. If the target method returns a
	 * non-null object, wrap in a JMS message and send it back.
	 * @param message the incoming JMS message
	 * @param session the JMS session to operate on
	 * @throws JMSException if thrown by JMS API methods
	 */
	public void onMessage(Message message, Session session) throws JMSException {
		Object convertedMessage = extractMessage(message);
		Object result = doOnMessage(convertedMessage);
		if (result != null) {
			handleResult(result, message, session);
		}
		else {
			if (LoggerAdapter.isDebugEnabled(this))
				LoggerAdapter.debug("No result object given - no result to handle", this);
		}
	}


    protected abstract Object doOnMessage(Object convertedMessage);

	/**
	 * Initialize the default implementations for the adapter's strategies.
	 * @see #setMessageConverter
	 * @see org.springframework.jms.support.converter.SimpleMessageConverter
	 */
	protected void initDefaultStrategies() {
		setMessageConverter(new SimpleMessageConverter());
	}

    /**
	 * Handle the given exception that arose during listener execution.
	 * The default implementation logs the exception at error level.
	 * <p>This method only applies when used as standard JMS {@link MessageListener}.
	 * In case of the Spring {@link SessionAwareMessageListener} mechanism,
	 * exceptions get handled by the caller instead.
	 * @param ex the exception to handle
	 * @see #onMessage(javax.jms.Message)
	 */
	protected void handleListenerException(Throwable ex) {
		LoggerAdapter.error("Listener execution failed", ex, this);
	}

	/**
	 * Extract the message body from the given JMS message.
	 * @param message the JMS <code>Message</code>
	 * @return the content of the message, to be passed into the
	 * listener method as argument
	 * @throws JMSException if thrown by JMS API methods
	 */
	protected Object extractMessage(Message message) throws JMSException {
		MessageConverter converter = getMessageConverter();
		if (converter != null) {
			return converter.fromMessage(message);
		}
		return message;
	}


	/**
	 * Handle the given result object returned from the listener method,
	 * sending a response message back.
	 * @param result the result object to handle (never <code>null</code>)
	 * @param request the original request message
	 * @param session the JMS Session to operate on (may be <code>null</code>)
	 * @throws JMSException if thrown by JMS API methods
	 * @see #buildMessage
	 * @see #postProcessResponse
	 * @see #getResponseDestination
	 * @see #sendResponse
	 */
	protected void handleResult(Object result, Message request, Session session) throws JMSException {
		if (session != null) {
			if (LoggerAdapter.isDebugEnabled(this)) {
				LoggerAdapter.debug("Listener method returned result [" + result +
						"] - generating response message for it", this);
			}
			Message response = buildMessage(session, result);
			postProcessResponse(request, response);
			Destination destination = getResponseDestination(request, response, session);
			sendResponse(session, destination,  response);
		}
		else {
			if (LoggerAdapter.isDebugEnabled(this)) {
				LoggerAdapter.debug("Listener method returned result [" + result +
						"]: not generating response message for it because of no JMS Session given",this);
			}
		}
	}

	/**
	 * Build a JMS message to be sent as response based on the given result object.
	 * @param session the JMS Session to operate on
	 * @param result the content of the message, as returned from the listener method
	 * @return the JMS <code>Message</code> (never <code>null</code>)
	 * @throws JMSException if thrown by JMS API methods
	 * @see #setMessageConverter
	 */
	protected Message buildMessage(Session session, Object result) throws JMSException {
		MessageConverter converter = getMessageConverter();
		if (converter != null) {
			return converter.toMessage(result, session);
		}
		else {
			if (!(result instanceof Message)) {
				throw new MessageConversionException(
						"No MessageConverter specified - cannot handle message [" + result + "]");
			}
			return (Message) result;
		}
	}

	/**
	 * Post-process the given response message before it will be sent.
	 * <p>The default implementation sets the response's correlation id
	 * to the request message's correlation id.
	 * @param request the original incoming JMS message
	 * @param response the outgoing JMS message about to be sent
	 * @throws JMSException if thrown by JMS API methods
	 * @see javax.jms.Message#setJMSCorrelationID
	 */
	protected void postProcessResponse(Message request, Message response) throws JMSException {
		response.setJMSCorrelationID(request.getJMSCorrelationID());
	}

	/**
	 * Determine a response destination for the given message.
	 * <p>The default implementation first checks the JMS Reply-To
	 * {@link Destination} of the supplied request; if that is not <code>null</code>
	 * it is returned; if it is <code>null</code>, then the configured
	 * {@link #resolveDefaultResponseDestination default response destination}
	 * is returned; if this too is <code>null</code>, then an
	 * {@link InvalidDestinationException} is thrown.
	 * @param request the original incoming JMS message
	 * @param response the outgoing JMS message about to be sent
	 * @param session the JMS Session to operate on
	 * @return the response destination (never <code>null</code>)
	 * @throws JMSException if thrown by JMS API methods
	 * @throws InvalidDestinationException if no {@link Destination} can be determined
	 * @see #setDefaultResponseDestination
	 * @see javax.jms.Message#getJMSReplyTo()
	 */
	protected Destination getResponseDestination(Message request, Message response, Session session)
			throws JMSException {

		Destination replyTo = request.getJMSReplyTo();
		if (replyTo == null) {
			replyTo = resolveDefaultResponseDestination(session);
			if (replyTo == null) {
				throw new InvalidDestinationException("Cannot determine response destination: " +
						"Request message does not contain reply-to destination, and no default response destination set.");
			}
		}
		return replyTo;
	}

	/**
	 * Resolve the default response destination into a JMS {@link Destination}, using this
	 * accessor's {@link DestinationResolver} in case of a destination name.
	 * @return the located {@link Destination}
	 * @throws javax.jms.JMSException if resolution failed
	 * @see #setDefaultResponseDestination
	 * @see #setDefaultResponseQueueName
	 * @see #setDefaultResponseTopicName
	 * @see #setDestinationResolver
	 */
	protected Destination resolveDefaultResponseDestination(Session session) throws JMSException {
		if (this.defaultResponseDestination instanceof Destination) {
			return (Destination) this.defaultResponseDestination;
		}
		if (this.defaultResponseDestination instanceof DestinationNameHolder) {
			DestinationNameHolder nameHolder = (DestinationNameHolder) this.defaultResponseDestination;
			return getDestinationResolver().resolveDestinationName(session, nameHolder.name, nameHolder.isTopic);
		}
		return null;
	}

	/**
	 * Send the given response message to the given destination.
	 * @param response the JMS message to send
	 * @param destination the JMS destination to send to
	 * @param session the JMS session to operate on
	 * @throws JMSException if thrown by JMS API methods
	 * @see #postProcessProducer
	 * @see javax.jms.Session#createProducer
	 * @see javax.jms.MessageProducer#send
	 */
	protected void sendResponse(Session session, Destination destination, Message response) throws JMSException {
		MessageProducer producer = session.createProducer(destination);
		try {
			postProcessProducer(producer, response);
			producer.send(response);
		}
		finally {
			JmsUtils.closeMessageProducer(producer);
		}
	}

	/**
	 * Post-process the given message producer before using it to send the response.
	 * <p>The default implementation is empty.
	 * @param producer the JMS message producer that will be used to send the message
	 * @param response the outgoing JMS message about to be sent
	 * @throws JMSException if thrown by JMS API methods
	 */
	protected void postProcessProducer(MessageProducer producer, Message response) throws JMSException {
	}


	/**
	 * Internal class combining a destination name
	 * and its target destination type (queue or topic).
	 */
	private static class DestinationNameHolder {

		public final String name;

		public final boolean isTopic;

		public DestinationNameHolder(String name, boolean isTopic) {
			this.name = name;
			this.isTopic = isTopic;
		}
	}

}
