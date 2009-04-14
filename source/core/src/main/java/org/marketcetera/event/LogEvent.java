package org.marketcetera.event;

import static org.marketcetera.event.LogEvent.Level.DEBUG;
import static org.marketcetera.event.LogEvent.Level.ERROR;
import static org.marketcetera.event.LogEvent.Level.INFO;
import static org.marketcetera.event.LogEvent.Level.WARN;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;
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
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Represents a log entry event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class LogEvent
        extends EventBase
{
    /**
     * Severity level of event.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    public static enum Level
    {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage0P inMessage,
                                 Level inLevel)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage1P inMessage,
                                 Level inLevel,
                                 Serializable inP1)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage2P inMessage,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage3P inMessage,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage4P inMessage,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage5P inMessage,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage6P inMessage,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessageNP inMessage,
                                 Level inLevel,
                                 Serializable...inParameters)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            null,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage0P inMessage,
                                 Throwable inException,
                                 Level inLevel)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage1P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage2P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage3P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage4P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage5P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessage6P inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     * @throws UnsupportedOperationException if the specified <code>Level</code> is not supported 
     */
    public static LogEvent event(I18NMessageNP inMessage,
                                 Throwable inException,
                                 Level inLevel,
                                 Serializable...inParameters)
    {
        return new LogEvent(inLevel,
                            inMessage,
                            inException,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage0P inMessage)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage1P inMessage,
                                 Serializable inP1)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage2P inMessage,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage3P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage4P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage5P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage6P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessageNP inMessage,
                                 Serializable...inParameters)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            null,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage0P inMessage,
                                 Throwable inException)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage1P inMessage,
                                 Throwable inException,
                                 Serializable inP1)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage2P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage3P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage4P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage5P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessage6P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of debug <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent debug(I18NMessageNP inMessage,
                                 Throwable inException,
                                 Serializable...inParameters)
    {
        return new LogEvent(DEBUG,
                            inMessage,
                            inException,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage0P inMessage)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage1P inMessage,
                                 Serializable inP1)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage2P inMessage,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage3P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage4P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage5P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage6P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessageNP inMessage,
                                 Serializable...inParameters)
    {
        return new LogEvent(INFO,
                            inMessage,
                            null,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage0P inMessage,
                                 Throwable inException)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage1P inMessage,
                                 Throwable inException,
                                 Serializable inP1)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage2P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage3P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage4P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage5P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessage6P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of info <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent info(I18NMessageNP inMessage,
                                 Throwable inException,
                                 Serializable...inParameters)
    {
        return new LogEvent(INFO,
                            inMessage,
                            inException,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage0P inMessage)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage1P inMessage,
                                 Serializable inP1)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage2P inMessage,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage3P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage4P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage5P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage6P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessageNP inMessage,
                                 Serializable...inParameters)
    {
        return new LogEvent(WARN,
                            inMessage,
                            null,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage0P inMessage,
                                 Throwable inException)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage1P inMessage,
                                 Throwable inException,
                                 Serializable inP1)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage2P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage3P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage4P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage5P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessage6P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of warn <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent warn(I18NMessageNP inMessage,
                                 Throwable inException,
                                 Serializable...inParameters)
    {
        return new LogEvent(WARN,
                            inMessage,
                            inException,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage0P inMessage)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            new Serializable[0]);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage1P inMessage,
                                 Serializable inP1)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage2P inMessage,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage3P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage4P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage5P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage6P inMessage,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessageNP inMessage,
                                 Serializable...inParameters)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            null,
                            inParameters);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage0P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage0P inMessage,
                                 Throwable inException)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage1P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage1P inMessage,
                                 Throwable inException,
                                 Serializable inP1)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage2P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage2P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1,
                            inP2);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage3P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage3P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage4P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage4P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage5P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage5P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessage6P</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inP1 a <code>Serializable</code> value containing the parameter for the message
     * @param inP2 a <code>Serializable</code> value containing the parameter for the message
     * @param inP3 a <code>Serializable</code> value containing the parameter for the message
     * @param inP4 a <code>Serializable</code> value containing the parameter for the message
     * @param inP5 a <code>Serializable</code> value containing the parameter for the message
     * @param inP6 a <code>Serializable</code> value containing the parameter for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessage6P inMessage,
                                 Throwable inException,
                                 Serializable inP1,
                                 Serializable inP2,
                                 Serializable inP3,
                                 Serializable inP4,
                                 Serializable inP5,
                                 Serializable inP6)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inP1,
                            inP2,
                            inP3,
                            inP4,
                            inP5,
                            inP6);
    }
    /**
     * Returns a <code>LogEvent</code> object of error <code>Level</code>. 
     *
     * @param inMessage an <code>I18NMessageNP</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent error(I18NMessageNP inMessage,
                                 Throwable inException,
                                 Serializable...inParameters)
    {
        return new LogEvent(ERROR,
                            inMessage,
                            inException,
                            inParameters);
    }
    /**
     * Determines if the given event should be logged or not.
     * 
     * @param inEvent a <code>LogEvent</code> value
     * @param category the log category
     * @return a <code>boolean</code> value
     */
    public static boolean shouldLog(LogEvent inEvent, String category) {
        if (Level.DEBUG.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isDebugEnabled(category);
        }
        if (Level.INFO.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isInfoEnabled(category);
        }
        if (Level.WARN.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isWarnEnabled(category);
        }
        if (Level.ERROR.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isErrorEnabled(category);
        }
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        return String.format("%s %s event %s", //$NON-NLS-1$
                             getTimestampAsDate(),
                             getLevel(),
                             getMessage());
    }
    /**
     * Create a new LogEvent instance.
     *
     * @param inLevel a <code>Level</code> value indicating the Level of the log event
     * @param inMessage an <code>I18NMessage</code> value containing the message to put in the log
     * @param inException a <code>Throwable</code> value containing an exception to attach to the log event
     * @param inParameters a <code>Serializable...</code> value containing the parameters for the message, if necessary
     */
    private LogEvent(Level inLevel,
                     I18NMessage inMessage,
                     Throwable inException,
                     Serializable...inParameters)
    {
        super(counter.incrementAndGet(),
              System.currentTimeMillis());
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
    public String getMessage()
    {
        if(serialized) {
            return boundMessage;
        }
        switch(message.getParamCount()) {
            case -1:
                return new I18NBoundMessageNP((I18NMessageNP)message,
                                              parameters).getText();
            case 0 :
                return new I18NBoundMessage0P((I18NMessage0P)message).getText();
            case 1 :
                return new I18NBoundMessage1P((I18NMessage1P)message,
                                              parameters[0]).getText();
            case 2 :
                return new I18NBoundMessage2P((I18NMessage2P)message,
                                              parameters[0],
                                              parameters[1]).getText();
            case 3 :
                return new I18NBoundMessage3P((I18NMessage3P)message,
                                              parameters[0],
                                              parameters[1],
                                              parameters[2]).getText();
            case 4 :
                return new I18NBoundMessage4P((I18NMessage4P)message,
                                              parameters[0],
                                              parameters[1],
                                              parameters[2],
                                              parameters[3]).getText();
            case 5 :
                return new I18NBoundMessage5P((I18NMessage5P)message,
                                              parameters[0],
                                              parameters[1],
                                              parameters[2],
                                              parameters[3],
                                              parameters[4]).getText();
            case 6 :
                return new I18NBoundMessage6P((I18NMessage6P)message,
                                              parameters[0],
                                              parameters[1],
                                              parameters[2],
                                              parameters[3],
                                              parameters[4],
                                              parameters[5]).getText();
        }
        throw new IllegalStateException();
    }
    /**
     * Get the Level value.
     *
     * @return a <code>Level</code> value
     */
    public Level getLevel()
    {
        return level;
    }
    /**
     * Get the exception value.
     *
     * @return a <code>Throwable</code> value
     */
    public Throwable getException()
    {
        return exception;
    }
    /**
     * @serialData The <code>Level</code>, <code>Exception</code>,
     * and <code>boundMessage</code> are emitted.
     */
    private void writeObject(ObjectOutputStream inStream)
        throws IOException
    {
        boundMessage = getMessage();
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
        serialized = true;
    }
    /**
     * counter for creating unique event identifiers
     */
    private transient static final AtomicLong counter = new AtomicLong(0);
    /**
     * the event Level
     */
    private final Level level;
    /**
     * the event exception or null
     */
    private final Throwable exception;
    /**
     * the unbound event message valid only before serialization
     */
    private transient final I18NMessage message;
    /**
     * the event parameters or null valid only before serialization
     */
    private transient final Serializable[] parameters;
    /**
     * the bound event message valid only after serialization
     */
    private String boundMessage;
    /**
     * indicates whether the object has been serialized or not
     */
    private transient boolean serialized = false;
    private static final long serialVersionUID = 1L;
}
