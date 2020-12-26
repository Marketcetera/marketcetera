package org.marketcetera.eventbus.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=EventBusServerTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera","com.marketcetera"})
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
public class EventBusServerEsperTest
{
    @Test
    public void testOne()
            throws Exception
    {
        
    }
}
