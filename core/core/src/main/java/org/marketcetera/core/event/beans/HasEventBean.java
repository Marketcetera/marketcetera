package org.marketcetera.core.event.beans;

/* $License$ */

/**
 * Indicates that the implementing class possesses an <code>EventBean</code> attribute
 * or has access to one.
 *
 * @version $Id: HasEventBean.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public interface HasEventBean
{
    /**
     * Gets the <code>EventBean</code> value.
     *
     * @return an <code>EventBean</code> value
     */
    public EventBean getEventBean();
}
