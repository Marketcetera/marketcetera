package org.marketcetera.fix.store;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provides common behavior for message store classes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@MappedSuperclass
public class AbstractMessageStoreEntity
        extends EntityBase
{
    /**
     * Get the sessionId value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionId()
    {
        return sessionId;
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>String</code> value
     */
    public void setSessionId(String inSessionId)
    {
        sessionId = inSessionId;
    }
    /**
     * session id value
     */
    @Column(name="session_id",nullable=false,unique=false)
    private String sessionId;
    private static final long serialVersionUID = -1738126720680914253L;
}
