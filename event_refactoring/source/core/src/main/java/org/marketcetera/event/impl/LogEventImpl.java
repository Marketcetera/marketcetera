package org.marketcetera.event.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.LogEvent;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.util.EventValidationServices;
import org.marketcetera.event.util.LogEventLevel;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NBoundMessage4P;
import org.marketcetera.util.log.I18NBoundMessage5P;
import org.marketcetera.util.log.I18NBoundMessage6P;
import org.marketcetera.util.log.I18NBoundMessageNP;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageNP;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.RemoteI18NBoundMessage;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

/* $License$ */

/**
 * Implements {@link LogEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
class LogEventImpl
        implements LogEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.LogEvent#getException()
     */
    @Override
    public Throwable getException()
    {
        return exception;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.LogEvent#getLevel()
     */
    @Override
    public LogEventLevel getLevel()
    {
        return level;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.LogEvent#getMessage()
     */
    @Override
    public String getMessage()
    {
        if(serialized) {
            return boundMessage.getText();
        } 
        return getI18NBoundMessage().getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
    }
    /**
     * Create a new LogEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inLevel a <code>LogEventLevel</code> value
     * @param inMessage an <code>I18NMessage</code> value
     * @param inException a <code>Throwable</code> value
     * @param inParameters a <code>Serializable...</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inLevel</code> is <code>null</code>
     */
    LogEventImpl(long inMessageId,
                 Date inTimestamp,
                 LogEventLevel inLevel,
                 I18NMessage inMessage,
                 Throwable inException,
                 Serializable... inParameters)
    {
        event.setMessageId(inMessageId);
        event.setTimestamp(inTimestamp);
        event.setDefaults();
        event.validate();
        if(inLevel == null) {
            EventValidationServices.error(VALIDATION_NULL_LOG_LEVEL);
        }
        level = inLevel;
        message = inMessage;
        exception = inException;
        parameters = inParameters;
    }
    /**
     * Returns the bound event message. 
     *
     * @return an <code>I18NBoundMessage</code> value
     * @throws IllegalStateException if the message takes an unexpected number of parameters
     */
    private I18NBoundMessage getI18NBoundMessage()
    {
        switch(message.getParamCount()) {
        case -1:
            return new I18NBoundMessageNP((I18NMessageNP)message,
                                          parameters);
        case 0 :
            return new I18NBoundMessage0P((I18NMessage0P)message);
        case 1 :
            return new I18NBoundMessage1P((I18NMessage1P)message,
                                          parameters[0]);
        case 2 :
            return new I18NBoundMessage2P((I18NMessage2P)message,
                                          parameters[0],
                                          parameters[1]);
        case 3 :
            return new I18NBoundMessage3P((I18NMessage3P)message,
                                          parameters[0],
                                          parameters[1],
                                          parameters[2]);
        case 4 :
            return new I18NBoundMessage4P((I18NMessage4P)message,
                                          parameters[0],
                                          parameters[1],
                                          parameters[2],
                                          parameters[3]);
        case 5 :
            return new I18NBoundMessage5P((I18NMessage5P)message,
                                          parameters[0],
                                          parameters[1],
                                          parameters[2],
                                          parameters[3],
                                          parameters[4]);
        case 6 :
            return new I18NBoundMessage6P((I18NMessage6P)message,
                                          parameters[0],
                                          parameters[1],
                                          parameters[2],
                                          parameters[3],
                                          parameters[4],
                                          parameters[5]);
        }
        throw new IllegalStateException();
    }
    /**
     * @serialData The <code>Level</code>, <code>exceptionInfo</code>,
     * and <code>boundMessage</code> are emitted.
     */
    private void writeObject(ObjectOutputStream inStream)
        throws IOException
    {
        boundMessage = new RemoteI18NBoundMessage(getI18NBoundMessage());
        exceptionInfo = new RemoteProperties(exception);
        inStream.defaultWriteObject();
    }
    /**
     * Creates the object from a serialized stream.
     *
     * @param inStream an <code>ObjectInputStream</code> value
     * @throws IOException if an error occurs
     * @throws ClassNotFoundException if an error occurs
     */
    private void readObject(ObjectInputStream inStream)
        throws IOException, ClassNotFoundException
    {
        inStream.defaultReadObject();
        exception = exceptionInfo.getThrowable();
        serialized = true;
    }
    /**
     * the event exception information or null valid only after serialization
     */
    private RemoteProperties exceptionInfo;
    /**
     * the log level
     */
    private final LogEventLevel level;
    /**
     * the bound event message valid only after serialization
     */
    private RemoteI18NBoundMessage boundMessage;
    /**
     * the exception value or null, valid only before serialization
     */
    private transient Throwable exception;
    /**
     * the unbound message, valid both before and after serialization
     */
    private final transient I18NMessage message;
    /**
     * the event attributes
     */
    private final EventBean event = new EventBean();
    /**
     * the message parameters, valid only before serialization
     */
    private final transient Serializable[] parameters;
    /**
     * indicates whether the object has been serialized or not
     */
    private transient boolean serialized = false;
    private static final long serialVersionUID = 1L;
}
