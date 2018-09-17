package org.marketcetera.quickfix;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.BatchQueueProcessor;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.IncomingMessage;
import org.marketcetera.fix.dao.PersistentIncomingMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Log;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Records incoming FIX messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RecordingLog
        implements Log
{
    /**
     * Create a new RecordingLog instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inConfiguration a <code>RecordingLogConfiguration</code> value
     * @param inEmbeddedLogFactory a <code>LogFactory</code> value
     */
    public RecordingLog(SessionID inSessionId,
                        RecordingLogConfiguration inConfiguration,
                        LogFactory inEmbeddedLogFactory)
    {
        embeddedLog = inEmbeddedLogFactory.create(inSessionId);
        sessionId = inSessionId;
        configuration = inConfiguration;
        if(configuration.getSessionNameProvider() == null) {
            sessionName = sessionId.toString();
        } else {
            sessionName = configuration.getSessionNameProvider().getSessionName(sessionId);
        }
        incomingMessageQueue = new IncomingMessageQueue();
        incomingMessageQueue.start();
    }
    /* (non-Javadoc)
     * @see quickfix.Log#clear()
     */
    @Override
    public void clear()
    {
        SLF4JLoggerProxy.debug(this,
                               "{} log clear invoked",
                               sessionName);
        embeddedLog.clear();
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onErrorEvent(java.lang.String)
     */
    @Override
    public void onErrorEvent(String inErrorEvent)
    {
        SLF4JLoggerProxy.warn(this,
                              "{} ERROR: {}",
                              sessionName,
                              inErrorEvent);
        embeddedLog.onErrorEvent(inErrorEvent);
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onEvent(java.lang.String)
     */
    @Override
    public void onEvent(String inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} event: {}",
                               sessionName,
                               inEvent);
        embeddedLog.onEvent(inEvent);
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onIncoming(java.lang.String)
     */
    @Override
    public void onIncoming(String inIncomingMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} incoming: {}",
                               sessionName,
                               inIncomingMessage);
        incomingMessageQueue.add(inIncomingMessage);
        embeddedLog.onIncoming(inIncomingMessage);
    }
    /* (non-Javadoc)
     * @see quickfix.Log#onOutgoing(java.lang.String)
     */
    @Override
    public void onOutgoing(String inOutgoingMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} outgoing: {}",
                               sessionName,
                               inOutgoingMessage);
        embeddedLog.onOutgoing(inOutgoingMessage);
    }
    /**
     * Processes incoming messages.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class IncomingMessageQueue
            extends BatchQueueProcessor<String>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(String inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#processData(java.util.Deque)
         */
        @Override
        protected void processData(Deque<String> inData)
                throws Exception
        {
            List<PersistentIncomingMessage> newMessages = new ArrayList<>();
            try {
                for(String rawMessage : inData) {
                    try {
                        Message fixMessage = new Message(rawMessage);
                        IncomingMessage incomingMessage = configuration.getIncomingMessageFactory().create(sessionId,
                                                                                                           fixMessage);
                        newMessages.add((PersistentIncomingMessage)incomingMessage);
                    } catch (Exception e) {
                        if(PlatformServices.isShutdown(e)) {
                            throw e;
                        }
                        if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                            SLF4JLoggerProxy.warn(RecordingLog.this,
                                                  e,
                                                  "On data process: {}",
                                                  ExceptionUtils.getRootCauseMessage(e));
                        } else {
                            SLF4JLoggerProxy.warn(RecordingLog.this,
                                                  "On data process: {}",
                                                  ExceptionUtils.getRootCauseMessage(e));
                        }
                    }
                }
                configuration.getIncomingMessageDao().saveAll(newMessages);
            } catch (Exception e) {
                if(PlatformServices.isShutdown(e)) {
                    // this exception can be safely ignored
                    return;
                }
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(RecordingLog.this,
                                          e,
                                          "On data process: {}",
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(RecordingLog.this,
                                          "On data process: {}",
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
        /**
         * Create a new IncomingMessageQueue instance.
         */
        private IncomingMessageQueue()
        {
            super("RecordLogProcessor-"+sessionName);
        }
    }
    /**
     * configuration supplying object
     */
    private final RecordingLogConfiguration configuration;
    /**
     * processes incoming messages
     */
    private final IncomingMessageQueue incomingMessageQueue;
    /**
     * session id for this log object
     */
    private final SessionID sessionId;
    /**
     * name of the session
     */
    private final String sessionName;
    /**
     * mix-in log used for additional log behaviors
     */
    private final Log embeddedLog;
}
