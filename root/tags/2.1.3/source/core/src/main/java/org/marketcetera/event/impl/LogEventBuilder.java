package org.marketcetera.event.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

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
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link LogEvent} objects.
 * 
 * <p>Construct a <code>LogEvent</code> by getting a <code>LogEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation with {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class LogEventBuilder
        extends AbstractEventBuilderImpl<LogEvent>
{
    /**
     * Returns a <code>LogEventBuilder</code> suitable for constructing a new <code>LogEvent</code> object
     * of type <code>debug</code>.
     *
     * @return a <code>LogEventBuilder</code> value
     */
    public static LogEventBuilder debug()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getEvent(),
                                        LogEventLevel.DEBUG,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * Returns a <code>LogEventBuilder</code> suitable for constructing a new <code>LogEvent</code> object
     * of type <code>info</code>.
     *
     * @return a <code>LogEventBuilder</code> value
     */
    public static LogEventBuilder info()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getEvent(),
                                        LogEventLevel.INFO,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * Returns a <code>LogEventBuilder</code> suitable for constructing a new <code>LogEvent</code> object
     * of type <code>warn</code>.
     *
     * @return a <code>LogEventBuilder</code> value
     */
    public static LogEventBuilder warn()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getEvent(),
                                        LogEventLevel.WARN,
                                        getMessage(),
                                        getException(),
                                        getParameters());
            }
        };
    }
    /**
     * Returns a <code>LogEventBuilder</code> suitable for constructing a new <code>LogEvent</code> object
     * of type <code>error</code>.
     *
     * @return a <code>LogEventBuilder</code> value
     */
    public static LogEventBuilder error()
    {
        return new LogEventBuilder() {
            @Override
            public LogEvent create()
            {
                return new LogEventImpl(getEvent(),
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
    public final LogEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public final LogEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderImpl#withSource(java.lang.Object)
     */
    @Override
    public LogEventBuilder withSource(Object inSource)
    {
        super.withSource(inSource);
        return this;
    }
    /**
     * Sets the exception to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * <code>Throwable</code> value from the builder, if any.
     *
     * @param inException a <code>Throwable</code> value or <code>null</code>
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withException(Throwable inException)
    {
        exception = inException;
        return this;
    }
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.
     *
     * @param inMessage an <code>I18NMessage0P</code> value or <code>null</code>
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage0P inMessage)
    {
        message = inMessage;
        return this;
    }
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage1P inMessage,
                                             Serializable inP1)
    {
        message = inMessage;
        setParameters(inP1);
        return this;
    }
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP2 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage2P inMessage,
                                             Serializable inP1,
                                             Serializable inP2)
    {
        message = inMessage;
        setParameters(inP1,
                      inP2);
        return this;
    }
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP2 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP3 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage3P inMessage,
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
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP2 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP3 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP4 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage4P inMessage,
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
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP2 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP3 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP4 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP5 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage5P inMessage,
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
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inP1 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP2 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP3 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP4 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP5 a <code>Serializable</code> value to use as a parameter to the given message
     * @param inP6 a <code>Serializable</code> value to use as a parameter to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessage6P inMessage,
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
    /**
     * Sets the message to use in the new <code>LogEvent</code> value. 
     *
     * <p>Setting this value to <code>null</code> will remove the existing
     * message value from the builder, if any.  The given parameters will replace
     * the existing parameters.
     *
     * @param inMessage an <code>I18NMessage1P</code> value or <code>null</code>
     * @param inParameters a <code>Serializable...</code> value to use as parameters to the given message
     * @return a <code>LogEventBuilder</code> value
     */
    public final LogEventBuilder withMessage(I18NMessageNP inMessage,
                                             Serializable...inParameters)
    {
        message = inMessage;
        setParameters(inParameters);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("LogEventBuilder [exception=%s, message=%s, parameters=%s, event=%s]", //$NON-NLS-1$
                             exception,
                             message,
                             Arrays.toString(parameters),
                             getEvent());
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
     * Gets the parameters. 
     *
     * @return a <code>Serializable[]</code> value
     */
    protected final Serializable[] getParameters()
    {
        return parameters;
    }
    /**
     * Sets the parameters, replacing the existing parameters.
     *
     * @param inParameters a <code>Serializable...</code> value
     */
    private void setParameters(Serializable...inParameters)
    {
        if(inParameters == null ||
           inParameters.length == 0) {
            parameters = new Serializable[0];
            return;
        }
        parameters = new Serializable[inParameters.length];
        System.arraycopy(inParameters,
                         0,
                         parameters,
                         0,
                         inParameters.length);
    }
    /**
     * the log exception 
     */
    private Throwable exception;
    /**
     * the log message
     */
    private I18NMessage message;
    /**
     * the log parameters
     */
    private Serializable[] parameters = new Serializable[0];
}
