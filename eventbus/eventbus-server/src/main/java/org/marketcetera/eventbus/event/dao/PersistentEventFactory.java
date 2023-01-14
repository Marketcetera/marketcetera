//
// this file is automatically generated
//
package org.marketcetera.eventbus.event.dao;

/* $License$ */

/**
 * Creates new {@link PersistentEvent} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentEventFactory
        implements org.marketcetera.eventbus.event.EventFactory
{
    /**
     * Create a new <code>org.marketcetera.eventbus.event.dao.PersistentEvent</code> instance.
     *
     * @return a <code>org.marketcetera.eventbus.event.dao.PersistentEvent</code> value
     */
    @Override
    public org.marketcetera.eventbus.event.dao.PersistentEvent create()
    {
        return new org.marketcetera.eventbus.event.dao.PersistentEvent();
    }
    /**
     * Create a new <code>org.marketcetera.eventbus.event.dao.PersistentEvent</code> instance from the given object.
     *
     * @param inEvent an <code>org.marketcetera.eventbus.event.dao.PersistentEvent</code> value
     * @return an <code>org.marketcetera.eventbus.event.dao.PersistentEvent</code> value
     */
    @Override
    public org.marketcetera.eventbus.event.dao.PersistentEvent create(org.marketcetera.eventbus.event.Event inPersistentEvent)
    {
        return new org.marketcetera.eventbus.event.dao.PersistentEvent(inPersistentEvent);
    }
}
