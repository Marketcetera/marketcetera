package org.marketcetera.event;

import java.io.Serializable;
import java.util.Date;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Event
    extends TimestampCarrier, Serializable, Messages
{
    public long getMessageId();
    public Date getTimestamp();
    public Object getSource();
    public void setSource(Object inSource);
}
