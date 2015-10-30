package org.marketcetera.quickfix;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Log;

/* $License$ */

/**
 * Provides an observable {@link Log} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ObservableLog
        implements Log
{
    /* (non-Javadoc)
     * @see quickfix.Log#clear()
     */
    @Override
    public void clear()
    {
        embeddedLog.clear();
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onErrorEvent(java.lang.String)
     */
    @Override
    public void onErrorEvent(String inMessage)
    {
        embeddedLog.onErrorEvent(inMessage);
        for(Log observer : observers) {
            try {
                observer.onErrorEvent(inMessage);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onEvent(java.lang.String)
     */
    @Override
    public void onEvent(String inMessage)
    {
        embeddedLog.onEvent(inMessage);
        for(Log observer : observers) {
            try {
                observer.onEvent(inMessage);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onIncoming(java.lang.String)
     */
    @Override
    public void onIncoming(String inMessage)
    {
        embeddedLog.onIncoming(inMessage);
        for(Log observer : observers) {
            try {
                observer.onIncoming(inMessage);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onOutgoing(java.lang.String)
     */
    @Override
    public void onOutgoing(String inMessage)
    {
        embeddedLog.onOutgoing(inMessage);
        for(Log observer : observers) {
            try {
                observer.onOutgoing(inMessage);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * Adds the given observer.
     *
     * @param inObserver a <code>Log</code> value
     */
    public void addObserver(Log inObserver)
    {
        observers.add(inObserver);
    }
    /**
     * Removes the given observer.
     *
     * @param inObserver a <code>Log</code> value
     */
    public void removeObserver(Log inObserver)
    {
        observers.remove(inObserver);
    }
    /**
     * Create a new ObservableLog instance.
     *
     * @param inLog a <code>Log</code> value
     */
    ObservableLog(Log inLog)
    {
        if(inLog == null) {
            throw new NullPointerException();
        }
        embeddedLog = inLog;
    }
    /**
     * stores observers
     */
    private final Deque<Log> observers = new ConcurrentLinkedDeque<>();
    /**
     * mix-in <code>Log</code> object
     */
    private final Log embeddedLog;
}
