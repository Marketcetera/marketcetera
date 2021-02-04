package org.marketcetera.event;

import java.util.UUID;

import org.marketcetera.core.HasUuid;

/* $License$ */

/**
 * Provides common behaviors for events that implement {@link HasUuid}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseUuid
        implements HasUuid
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasUuid#getUuid()
     */
    @Override
    public String getUuid()
    {
        return uuid;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("UuidEvent [uuid=").append(uuid).append("]");
        return builder.toString();
    }
    /**
     * Create a new BaseUuid instance.
     */
    protected BaseUuid()
    {
        this(UUID.randomUUID().toString());
    }
    /**
     * Create a new BaseUuid instance.
     *
     * @param inUuid a <code>String</code> value
     */
    protected BaseUuid(String inUuid)
    {
        uuid = inUuid;
    }
    /**
     * immutable UUID value
     */
    private final String uuid;
}
