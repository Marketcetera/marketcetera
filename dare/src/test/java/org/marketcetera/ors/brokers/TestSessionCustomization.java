package org.marketcetera.ors.brokers;

import java.util.UUID;

import org.marketcetera.fix.provisioning.SimpleSessionCustomization;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Test session customization.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class TestSessionCustomization
        extends SimpleSessionCustomization
{
    /**
     * Create a new TestSessionCustomization instance.
     */
    TestSessionCustomization()
    {
        setName(testName);
    }
    /**
     * unique name for this customization
     */
    private final String testName = UUID.randomUUID().toString();
}