package org.marketcetera.fix.store;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;
import quickfix.MessageStore;
import quickfix.SessionID;

/**
 * Stub implementation of HibernateMessageStore for Spring Boot 3 migration.
 * 
 * <p>Original implementation required QueryDSL, which is not compatible with Jakarta EE.
 * 
 * <p>See HibernateMessageStore.java.disabled for the original implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public class HibernateMessageStore
        implements MessageStore
{
    /**
     * Create a new HibernateMessageStore instance.
     */
    public HibernateMessageStore(SessionID inSessionID)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation for Spring Boot 3 migration");
        sessionID = inSessionID;
    }
    
    @Override
    public boolean set(int inSequence, String inMessage) throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - set not implemented");
        return false;
    }
    
    @Override
    public void get(int inStartSequence, int inEndSequence, java.util.Collection<String> inMessages) throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - get not implemented");
    }
    
    @Override
    public int getNextSenderMsgSeqNum() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getNextSenderMsgSeqNum not implemented");
        return 1;
    }
    
    @Override
    public int getNextTargetMsgSeqNum() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getNextTargetMsgSeqNum not implemented");
        return 1;
    }
    
    @Override
    public void setNextSenderMsgSeqNum(int inNextSenderMsgSeqNum) throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - setNextSenderMsgSeqNum not implemented");
    }
    
    @Override
    public void setNextTargetMsgSeqNum(int inNextTargetMsgSeqNum) throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - setNextTargetMsgSeqNum not implemented");
    }
    
    @Override
    public void incrNextSenderMsgSeqNum() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - incrNextSenderMsgSeqNum not implemented");
    }
    
    @Override
    public void incrNextTargetMsgSeqNum() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - incrNextTargetMsgSeqNum not implemented");
    }
    
    @Override
    public Date getCreationTime() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getCreationTime not implemented");
        return new Date();
    }
    
    @Override
    public void reset() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - reset not implemented");
    }
    
    @Override
    public void refresh() throws IOException
    {
        SLF4JLoggerProxy.warn(this, "Stub implementation - refresh not implemented");
    }
    
    /**
     * The session ID value.
     */
    private final SessionID sessionID;
}