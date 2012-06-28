package org.marketcetera.event.beans;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class possesses an <code>EventBean</code> attribute
 * or has access to one.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasEventBean.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: HasEventBean.java 16063 2012-01-31 18:21:55Z colin $")
public interface HasEventBean
{
    /**
     * Gets the <code>EventBean</code> value.
     *
     * @return an <code>EventBean</code> value
     */
    public EventBean getEventBean();
}
