//
// this file is automatically generated
//
package org.marketcetera.eventbus.data.event;

/* $License$ */

/**
 * Creates new {@link SimpleDataEvent} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleDataEventFactory
        implements org.marketcetera.eventbus.data.event.DataEventFactory
{
    /**
     * Create a new <code>org.marketcetera.eventbus.data.event.SimpleDataEvent</code> instance.
     *
     * @return a <code>org.marketcetera.eventbus.data.event.SimpleDataEvent</code> value
     */
    @Override
    public org.marketcetera.eventbus.data.event.SimpleDataEvent create()
    {
        return new org.marketcetera.eventbus.data.event.SimpleDataEvent();
    }
    /**
     * Create a new <code>org.marketcetera.eventbus.data.event.SimpleDataEvent</code> instance from the given object.
     *
     * @param inObject a <code>org.marketcetera.eventbus.data.event.SimpleDataEvent</code> value
     * @return a <code>org.marketcetera.eventbus.data.event.SimpleDataEvent</code> value
     */
    @Override
    public org.marketcetera.eventbus.data.event.SimpleDataEvent create(org.marketcetera.eventbus.data.event.DataEvent inSimpleDataEvent)
    {
        return new org.marketcetera.eventbus.data.event.SimpleDataEvent(inSimpleDataEvent);
    }
}
