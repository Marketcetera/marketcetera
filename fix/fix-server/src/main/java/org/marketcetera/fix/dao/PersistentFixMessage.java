package org.marketcetera.fix.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.marketcetera.fix.FixMessage;
import org.marketcetera.persist.EntityBase;

import quickfix.Message;

/* $License$ */

/**
 * Provides a persistent <code>FixMessage</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentFixMessage.java 17316 2017-07-17 15:46:23Z colin $
 * @since 2.5.0
 */
@Table(name="fix_messages")
@Entity(name="FixMessage")
public class PersistentFixMessage
        extends EntityBase
        implements FixMessage
{
    /**
     * Create a new PersistentFixMessage instance.
     */
    public PersistentFixMessage()
    {
    }
    /**
     * Create a new PersistentFixMessage instance.
     *
     * @param inFixMessage
     */
    public PersistentFixMessage(FixMessage inFixMessage)
    {
        message = inFixMessage.getMessage();
    }
    /**
     * Create a new PersistentFixMessage instance.
     *
     * @param inMessage a <code>Message</code> value
     */
    public PersistentFixMessage(Message inMessage)
    {
        message = inMessage.toString();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.FixMessage#getMessage()
     */
    @Override
    public String getMessage()
    {
        return message;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.FixMessage#setMessage(java.lang.String)
     */
    @Override
    public void setMessage(String inMessage)
    {
        message = inMessage;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.history.FixMessage#setMessage(quickfix.Message)
     */
    @Override
    public void setMessage(Message inMessage)
    {
        message = inMessage.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentFixMessage ").append(getId()).append(" [").append(message).append(']');
        return builder.toString();
    }
    /**
     * fix message value
     */
    @Lob
    @Column(name="message",nullable=false,length=8192,unique=false)
    private String message;
    private static final long serialVersionUID = -4643351730163899005L;
}
