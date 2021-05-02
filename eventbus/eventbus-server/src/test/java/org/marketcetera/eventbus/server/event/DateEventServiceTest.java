package org.marketcetera.eventbus.server.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.data.event.DataEvent;
import org.marketcetera.eventbus.data.event.DataEventChangeType;
import org.marketcetera.eventbus.data.event.DataEventService;
import org.marketcetera.eventbus.test.AbstractMockDataEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ExtendWith(SpringExtension.class)
@EntityScan(basePackages={"org.marketcetera"})
@ComponentScan(basePackages={"org.marketcetera"})
@SpringBootTest(classes=EventBusEventServerTestConfiguration.class)
public class DateEventServiceTest
{
    @BeforeEach
    public void setup()
            throws Exception
    {
        eventBusService.register(this);
        eventBusEvents.clear();
    }
    @Subscribe
    public void accept(DataEvent inDataEvent)
    {
        eventBusEvents.addLast(inDataEvent);
    }
    private DataEvent waitForEventBusEvent()
            throws Exception
    {
        DataEvent nextEvent = eventBusEvents.pollFirst(10,
                                                       TimeUnit.SECONDS);
        assertNotNull(nextEvent);
        return nextEvent;
    }
    // TODO -subscribe to any event-
    // TODO subscribe to this event not that event
    // TODO subscribe to superclass event
    // TODO subscribe to multiple events
    // TODO subscribe to multiple events with overlap (single notification)
    // TODO subscribe with duplicate id
    // TODO subscribe and pick up previous event via timestamp
    // TODO unsubscribe
    @Test
    public void testNoFiltering()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               null,
                                               consumer);
        DataEvent submittedEvent = generateAndSubmitEvent();
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(submittedEvent,
                        eventBusEvent,
                        consumerEvent);
    }
    @Test
    public void testSingleSpecificType()
            throws Exception
    {
        
    }
    private void assertSameEvent(DataEvent inExpectedEvent,
                                 DataEvent...inActualEvents)
            throws Exception
    {
        for(DataEvent actualEvent : inActualEvents) {
            assertEquals(inExpectedEvent.getId(),
                         actualEvent.getId());
        }
    }
    private DataEvent generateAndSubmitEvent()
    {
        DataEvent event = new AbstractMockDataEvent() {};
        event.setChangeType(DataEventChangeType.ADD);
        event.setTimestamp(new Date());
        SLF4JLoggerProxy.debug(this,
                               "Generating and submitting: {}",
                               event);
        eventBusService.post(event);
        return event;
    }
    private class DataEventConsumer
            implements Consumer<DataEvent>
    {
        /* (non-Javadoc)
         * @see java.util.function.Consumer#accept(java.lang.Object)
         */
        @Override
        public void accept(DataEvent inEvent)
        {
            events.addLast(inEvent);
        }
        private DataEvent waitForEvent()
                throws Exception
        {
            DataEvent event = events.pollFirst(10,
                                               TimeUnit.SECONDS);
            assertNotNull(event);
            return event;
        }
        /**
         * Reset the recorded data for this object.
         */
        private void reset()
        {
            events.clear();
        }
        /**
         * holds all events
         */
        private final BlockingDeque<DataEvent> events = new LinkedBlockingDeque<>();
    }
    private final BlockingDeque<DataEvent> eventBusEvents = new LinkedBlockingDeque<DataEvent>();
    @Autowired
    private EventBusService eventBusService;
    /**
     * provides access to data event services
     */
    @Autowired
    private DataEventService dataEventService;
}
