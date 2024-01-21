package org.marketcetera.eventbus.server;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketcetera.eventbus.EventBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Test the Esper &lt;-&gt; EventBus connector.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Disabled
@ExtendWith(SpringExtension.class)
@EntityScan(basePackages={"org.marketcetera"})
@ComponentScan(basePackages={"org.marketcetera"})
@SpringBootTest(classes=EventBusServerTestConfiguration.class)
public class EventBusServerEsperTest
{
    /**
     * Test that events are processed properly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEvents()
            throws Exception
    {
        doEventTest(new TestAutoregisteredEventBean());
        doEventTest(new TestManuallyRegisteredEventBean());
    }
    /**
     * Execute an event test.
     *
     * @param inEvent a <code>HasId</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doEventTest(final HasId inEvent)
            throws Exception
    {
        String matchingEpl = "select * from " + inEvent.getClass().getSimpleName();
        EsperQueryMetaData queryMetaData = esperEngine.deployStatement(matchingEpl);
        final Map<String,Boolean> matchingEventsReceived = Maps.newHashMap();
        matchingEventsReceived.put(inEvent.getId(),
                                   false);
        queryMetaData.getEsperStatement().addListener( (newData, oldData, statement, runtime) -> {
            String id = (String)newData[0].get("id");
            matchingEventsReceived.put(id,
                                       true);
        });
        eventBusService.post(inEvent);
        Assertions.assertTrue(matchingEventsReceived.get(inEvent.getId()));
        matchingEventsReceived.put(inEvent.getId(),
                                   false);
        eventBusService.post(this);
        Assertions.assertFalse(matchingEventsReceived.get(inEvent.getId()));
        esperEngine.undeployStatement(queryMetaData);
        matchingEventsReceived.clear();
        eventBusService.post(inEvent);
        Assertions.assertTrue(matchingEventsReceived.isEmpty());
    }
    /**
     * provides access to the default Esper engine
     */
    @Autowired
    private EsperEngine esperEngine;
    /**
     * provides access to the default event bus
     */
    @Autowired
    private EventBusService eventBusService;
}
