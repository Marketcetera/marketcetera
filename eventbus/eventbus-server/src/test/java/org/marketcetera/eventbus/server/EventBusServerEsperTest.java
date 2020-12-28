package org.marketcetera.eventbus.server;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketcetera.eventbus.EventBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPStatement;

/* $License$ */

/**
 * Test the Esper &lt;-&gt; EventBus connector.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ExtendWith(SpringExtension.class)
@EntityScan(basePackages={"org.marketcetera"})
@ComponentScan(basePackages={"org.marketcetera"})
@SpringBootTest(classes=EventBusServerTestConfiguration.class)
public class EventBusServerEsperTest
{
    /**
     * Test that an auto-registered event is processed properly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAutoregisteredEvent()
            throws Exception
    {
        String matchingEpl1 = "select * from " + TestEventBean.class.getSimpleName();
        final String matchingEpl1Id = UUID.randomUUID().toString();
        CompilerArguments args = new CompilerArguments(esperEngine.getConfiguration());
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled1 = compiler.compile("@name('" + matchingEpl1Id + "') " + matchingEpl1,
                                                  args);
        EPDeployment deployment1 = esperEngine.getRuntime().getDeploymentService().deploy(epCompiled1);
        // create a registered event type
        final TestEventBean event1 = new TestEventBean();
        EPStatement statement1 = esperEngine.getRuntime().getDeploymentService().getStatement(deployment1.getDeploymentId(),
                                                                                              matchingEpl1Id);
        final AtomicBoolean matchingEventReceived1 = new AtomicBoolean(false);
        statement1.addListener( (newData, oldData, theStatement, runtime) -> {
            String id = (String)newData[0].get("id");
            matchingEventReceived1.set(event1.getId().equals(id));
        });
        eventBusService.post(event1);
        Assertions.assertTrue(matchingEventReceived1.get());
        matchingEventReceived1.set(false);
        eventBusService.post(this);
        Assertions.assertFalse(matchingEventReceived1.get());
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
