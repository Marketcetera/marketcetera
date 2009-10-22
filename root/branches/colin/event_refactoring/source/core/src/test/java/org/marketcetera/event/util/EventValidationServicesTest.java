package org.marketcetera.event.util;

import org.junit.Test;
import org.marketcetera.event.Messages;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link EventValidationServices}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventValidationServicesTest
        implements Messages
{
    /**
     * Tests {@link EventValidationServices#error(org.marketcetera.util.log.I18NBoundMessage)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void error()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            protected void run()
                    throws Exception
            {
                EventValidationServices.error(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_QUOTE_ACTION.getText()) {
            protected void run()
                    throws Exception
            {
                EventValidationServices.error(VALIDATION_NULL_QUOTE_ACTION);
            }
        };
    }
}
