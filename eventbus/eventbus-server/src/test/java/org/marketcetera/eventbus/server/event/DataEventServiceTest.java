package org.marketcetera.eventbus.server.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.marketcetera.eventbus.data.event.DataEventServiceImpl;
import org.marketcetera.eventbus.test.AbstractMockDataEvent;
import org.marketcetera.eventbus.test.MockDataEventType1;
import org.marketcetera.eventbus.test.MockDataEventType2;
import org.marketcetera.module.ExpectedFailure;
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
public class DataEventServiceTest
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeEach
    public void setup()
            throws Exception
    {
        eventBusService.register(this);
        eventBusEvents.clear();
    }
    /**
     * Receives {@link DataEvent} types directly from the system event bus.
     *
     * @param inDataEvent a <code>DataEvent</code> value
     */
    @Subscribe
    public void accept(DataEvent inDataEvent)
    {
        eventBusEvents.addLast(inDataEvent);
    }
    // TODO unsubscribe
    // TODO consumer throws exception during accept
    // TODO subscribe for non-DataEvent type
    /**
     * Tests that events generated after the anchor timestamp are delivered.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEventRaceCondition()
            throws Exception
    {
        int serviceTtl = ((DataEventServiceImpl)dataEventService).getDataEventCacheTtlSeconds();
        DataEvent event0 = generateAndSubmitEvent(1);
        waitForEventBusEvent();
        Thread.sleep(serviceTtl+1);
        Date timestamp1 = new Date();
        Thread.sleep(1000);
        DataEvent event1 = generateAndSubmitEvent(1);
        Thread.sleep(1000);
        DataEvent event2 = generateAndSubmitEvent(0);
        Thread.sleep(1000);
        DataEvent event3 = generateAndSubmitEvent(1);
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        Thread.sleep(1000);
        consumer.assertNoEvents();
        dataEventService.subscribeToDataEvents(requestId,
                                               timestamp1,
                                               consumer,
                                               event1.getClass());
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(event1,
                        eventBusEvent,
                        consumerEvent);
        eventBusEvent = waitForEventBusEvent();
        eventBusEvent = waitForEventBusEvent();
        consumerEvent = consumer.waitForEvent();
        assertSameEvent(event3,
                        eventBusEvent,
                        consumerEvent);
        assertTrue(event0.getClass().isAssignableFrom(event1.getClass()));
        assertFalse(event0.getClass().isAssignableFrom(event2.getClass()));
        assertTrue(event0.getClass().isAssignableFrom(event3.getClass()));
        consumer.assertNoEvents();
    }
    /**
     * Tests handling of a duplicate request id.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDuplicateId()
            throws Exception
    {
        final String requestId = PlatformServices.generateId();
        final DataEventConsumer consumer = new DataEventConsumer();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer);
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                dataEventService.subscribeToDataEvents(requestId,
                                                       new Date(),
                                                       consumer);
            }
        };
    }
    /**
     * Tests handling of requests with no filter types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAllEvents()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
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
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        DataEvent generatedEvent = generateEvent(0);
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer,
                                               generatedEvent.getClass());
        submitEvent(generatedEvent);
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent,
                        eventBusEvent,
                        consumerEvent);
        consumer.reset();
        // generate an event of another type
        generatedEvent = generateEvent(1);
        submitEvent(generatedEvent);
        eventBusEvent = waitForEventBusEvent();
        assertSameEvent(generatedEvent,
                        eventBusEvent);
        consumer.assertNoEvents();
        eventBusEvents.clear();
        consumer.reset();
        // receive another event from the same type
        generatedEvent = generateAndSubmitEvent(0);
        eventBusEvent = waitForEventBusEvent();
        consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent,
                        eventBusEvent,
                        consumerEvent);
    }
    @Test
    public void testIgnoreWrongType()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        DataEvent generatedEvent0 = generateEvent(0);
        DataEvent generatedEvent1 = generateEvent(1);
        assertNotEquals(generatedEvent0.getClass().getSimpleName(),
                        generatedEvent1.getClass().getSimpleName());
        consumer.assertNoEvents();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer,
                                               generatedEvent0.getClass());
        submitEvent(generatedEvent1);
        DataEvent eventBusEvent = waitForEventBusEvent();
        assertSameEvent(generatedEvent1,
                        eventBusEvent);
        consumer.assertNoEvents();
    }
    @Test
    public void testSubscribeToSuperclass()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        DataEvent generatedEvent0 = generateEvent(0);
        DataEvent generatedEvent1 = generateEvent(1);
        consumer.assertNoEvents();
        assertTrue(DataEvent.class.isAssignableFrom(generatedEvent0.getClass()));
        assertTrue(DataEvent.class.isAssignableFrom(generatedEvent1.getClass()));
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer,
                                               DataEvent.class);
        submitEvent(generatedEvent0);
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent0,
                        eventBusEvent,
                        consumerEvent);
        consumer.reset();
        eventBusEvents.clear();
        submitEvent(generatedEvent1);
        eventBusEvent = waitForEventBusEvent();
        consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent1,
                        eventBusEvent,
                        consumerEvent);
    }
    @Test
    public void testSubscribeToMultipleTypes()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        DataEvent generatedEvent0 = generateEvent(0);
        DataEvent generatedEvent1 = generateEvent(1);
        assertFalse(generatedEvent0.getClass().isAssignableFrom(generatedEvent1.getClass()));
        assertFalse(generatedEvent1.getClass().isAssignableFrom(generatedEvent0.getClass()));
        consumer.assertNoEvents();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer,
                                               generatedEvent0.getClass(),
                                               generatedEvent1.getClass());
        submitEvent(generatedEvent0);
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent0,
                        eventBusEvent,
                        consumerEvent);
        consumer.reset();
        eventBusEvents.clear();
        submitEvent(generatedEvent1);
        eventBusEvent = waitForEventBusEvent();
        consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent1,
                        eventBusEvent,
                        consumerEvent);
    }
    @Test
    public void testSubscribeToMultipleOverlappingTypes()
            throws Exception
    {
        String requestId = PlatformServices.generateId();
        DataEventConsumer consumer = new DataEventConsumer();
        DataEvent generatedEvent0 = generateEvent(0);
        consumer.assertNoEvents();
        dataEventService.subscribeToDataEvents(requestId,
                                               new Date(),
                                               consumer,
                                               generatedEvent0.getClass(),
                                               DataEvent.class);
        submitEvent(generatedEvent0);
        DataEvent eventBusEvent = waitForEventBusEvent();
        DataEvent consumerEvent = consumer.waitForEvent();
        assertSameEvent(generatedEvent0,
                        eventBusEvent,
                        consumerEvent);
        consumer.assertNoEvents();
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
    /**
     * Wait a reasonable amount of time for the next {@link DataEvent} to be received on the system
     * event bus and return it.
     *
     * @return a <code>DataEvent</code>
     * @throws Exception if the event has not been received
     */
    private DataEvent waitForEventBusEvent()
            throws Exception
    {
        DataEvent nextEvent = eventBusEvents.pollFirst(10,
                                                       TimeUnit.SECONDS);
        assertNotNull(nextEvent);
        return nextEvent;
    }
    private DataEvent generateEvent()
    {
        DataEvent event = new AbstractMockDataEvent() {};
        event.setChangeType(DataEventChangeType.ADD);
        event.setTimestamp(new Date());
        return event;
    }
    private DataEvent generateEvent(int inType)
    {
        DataEvent event;
        switch(inType) {
            case 0:
                event = new MockDataEventType1();
                break;
            case 1:
                event = new MockDataEventType2();
                break;
            case 2:
            default:
                event = new AbstractMockDataEvent() {};
                break;
        }
        event.setChangeType(DataEventChangeType.ADD);
        event.setTimestamp(new Date());
        return event;
    }
    private DataEvent generateAndSubmitEvent()
    {
        DataEvent event = generateEvent(2);
        SLF4JLoggerProxy.debug(this,
                               "Generating and submitting: {}",
                               event);
        submitEvent(event);
        return event;
    }
    private DataEvent generateAndSubmitEvent(int inEventType)
    {
        DataEvent event = generateEvent(inEventType);
        SLF4JLoggerProxy.debug(this,
                               "Generating and submitting: {}",
                               event);
        submitEvent(event);
        return event;
    }
    private void submitEvent(DataEvent inEvent)
    {
        eventBusService.post(inEvent);
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
        private void assertNoEvents()
                throws Exception
        {
            assertTrue(events.isEmpty());
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
