package org.marketcetera.eventbus;

import java.util.Date;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataEvent
{
    EventAction getEventAction();
    void setEventAction(EventAction inEventAction);
    long getId();
    void setId(long inId);
    Date getTimestamp();
    void setTimestamp(Date inTimestamp);
    Class<?> getType();
    void setType(Class<?> inType);
}
