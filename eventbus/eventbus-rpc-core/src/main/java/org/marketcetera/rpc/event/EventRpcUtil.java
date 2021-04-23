package org.marketcetera.rpc.event;

import java.util.Optional;

import org.marketcetera.eventbus.DataEvent;
import org.marketcetera.eventbus.EventAction;
import org.marketcetera.eventbus.DataEventFactory;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides Event RPC utilities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventRpcUtil
{
    /**
     * Get the data event value from the given RPC value.
     *
     * @param inRpcEvent an <code>EventRpc.DataEvent</code> value
     * @param inDataEventFactory a <code>DataEventFactory</code> value
     * @return an <code>Optional&lt;Event&gt;</code> value
     */
    public static Optional<DataEvent> getEvent(EventRpc.DataEvent inRpcEvent,
                                               DataEventFactory inDataEventFactory)
    {
        if(inRpcEvent == null) {
            return Optional.empty();
        }
        DataEvent dataEvent = inDataEventFactory.create();
        dataEvent.setEventAction(EventAction.valueOf(inRpcEvent.getAction().name()));
        dataEvent.setId(inRpcEvent.getId());
        BaseRpcUtil.getDateValue(inRpcEvent.getTimestamp()).ifPresent(timestamp -> dataEvent.setTimestamp(timestamp));
        try {
            dataEvent.setType(Class.forName(inRpcEvent.getType()));
        } catch (ClassNotFoundException e) {
            SLF4JLoggerProxy.warn(EventRpcUtil.class,
                                  "No class for {}",
                                  inRpcEvent.getType());
        }
        return Optional.of(dataEvent);
    }
    public static Optional<EventRpc.DataEvent> getRpcDataEvent(DataEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        EventRpc.DataEvent.Builder builder = EventRpc.DataEvent.newBuilder();
        builder.setAction(EventRpc.EventAction.valueOf(inEvent.getEventAction().name()));
        throw new UnsupportedOperationException(); // TODO
    }
}
