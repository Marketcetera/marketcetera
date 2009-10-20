package org.marketcetera.event.impl;

import java.io.Serializable;
import java.util.Date;

import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageNP;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class LogEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<LogEvent>
{
    /**
     * 
     *
     *
     * @return
     */
    public static LogEventBuilder debug()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getMessageId(),
                                        getTimestamp(),
                                        LogEventLevel.DEBUG,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * 
     *
     *
     * @return
     */
    public static LogEventBuilder info()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getMessageId(),
                                        getTimestamp(),
                                        LogEventLevel.INFO,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * 
     *
     *
     * @return
     */
    public static LogEventBuilder warn()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getMessageId(),
                                        getTimestamp(),
                                        LogEventLevel.WARN,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * 
     *
     *
     * @return
     */
    public static LogEventBuilder error()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getMessageId(),
                                        getTimestamp(),
                                        LogEventLevel.ERROR,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withMessageId(long)
     */
    @Override
    public LogEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public LogEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    public LogEventBuilder withException(Throwable inException)
    {
        exception = inException;
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage0P inMessage)
    {
        message = inMessage;
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage1P inMessage,
                                       Serializable inP1)
    {
        message = inMessage;
        setParameters(inP1);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage2P inMessage,
                                       Serializable inP1,
                                       Serializable inP2)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage3P inMessage,
                                       Serializable inP1,
                                       Serializable inP2,
                                       Serializable inP3)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2,
                      inP3);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage4P inMessage,
                                       Serializable inP1,
                                       Serializable inP2,
                                       Serializable inP3,
                                       Serializable inP4)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2,
                      inP3,
                      inP4);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage5P inMessage,
                                       Serializable inP1,
                                       Serializable inP2,
                                       Serializable inP3,
                                       Serializable inP4,
                                       Serializable inP5)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2,
                      inP3,
                      inP4,
                      inP5);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessage6P inMessage,
                                       Serializable inP1,
                                       Serializable inP2,
                                       Serializable inP3,
                                       Serializable inP4,
                                       Serializable inP5,
                                       Serializable inP6)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2,
                      inP3,
                      inP4,
                      inP5,
                      inP6);
        return this;
    }
    public LogEventBuilder withMessage(I18NMessageNP inMessage,
                                       Serializable...inParameters)
    {
        message = inMessage;
        setParameters(inParameters);
        return this;
    }
    /**
     * Get the exception value.
     *
     * @return a <code>Throwable</code> value
     */
    protected final Throwable getException()
    {
        return exception;
    }
    /**
     * Get the message value.
     *
     * @return a <code>I18NMessage</code> value
     */
    protected final I18NMessage getMessage()
    {
        return message;
    }
    /**
     * Get the remoteProperties value.
     *
     * @return a <code>RemoteProperties</code> value
     */
    protected final RemoteProperties getRemoteProperties()
    {
        return remoteProperties;
    }
    /**
     * 
     *
     *
     * @return
     */
    protected final Serializable[] getParameters()
    {
        return parameters;
    }
    /**
     * 
     *
     *
     * @param inParameters
     */
    private void setParameters(Serializable...inParameters)
    {
        parameters = inParameters;
    }
    /**
     * 
     */
    private Throwable exception;
    /**
     * 
     */
    private I18NMessage message;
    /**
     * 
     */
    private RemoteProperties remoteProperties;
    /**
     * 
     */
    private Serializable[] parameters = new Serializable[0];
}
