/**
 * <p>Spring-wrapped JMS utilities.</p>
 * 
 * <p>The classes in this package provide utilities to create
 * Spring-wrapped JMS connections with minimal use of Spring and
 * JMS. This is done in the following steps:</p>
 * 
 * <ol>
 * 
 * <li><p>Initialize an instance of {@link
 * org.marketcetera.client.jms.JmsManager} using standard JMS connection
 * factories supplied via Spring configuration.</p></li>
 * 
 * <li><p>Retrieve from the {@link org.marketcetera.client.jms.JmsManager}
 * its outgoing connection manager {@link
 * org.marketcetera.client.jms.OutgoingJmsFactory}, and use its methods to
 * obtain a Spring JMS template, which you then use to send
 * messages.</p></li>
 * 
 * <li><p>Retrieve from the {@link org.marketcetera.client.jms.JmsManager}
 * its incoming connection manager {@link
 * org.marketcetera.client.jms.IncomingJmsFactory} to register a handler of
 * your own making (that implements {@link
 * org.marketcetera.client.jms.ReplyHandler} or {@link
 * org.marketcetera.client.jms.ReceiveOnlyHandler}); your handler will be
 * called when messages are received.</p></li>
 * 
 * </ol>
 * 
 * <p>This package supports both queues and topics.</p>
 *
 * <p>Additional classes provide JMS serialization (message
 * converters) of certain object types, as well as serialization
 * wrappers.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@XmlSchema(namespace="http://marketcetera.org/types/client")
package org.marketcetera.client.jms;

import javax.xml.bind.annotation.XmlSchema;
