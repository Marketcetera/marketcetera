package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;
import org.marketcetera.event.util.LogEventLevel;
import org.marketcetera.util.log.I18NMessage;

/* $License$ */

/**
 * Tests {@link LogEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class LogEventTest
{
    /**
     * Verifies that the event created contains the expected information.
     *
     * @param inActualEvent a <code>LogEvent</code> value containing the event to verify
     * @param inExpectedLevel a <code>Priority</code> value containing the expected priority
     * @param inException a <code>Throwable</code> value containing the expected exception or <code>null</code> for none
     * @param inExpectedMessage an <code>I18NMessage</code> value containing the expected message
     * @param inExpectedParameters a <code>Serializable[]</code> value containing the expected parameters
     */
    public static void verifyEvent(LogEvent inActualEvent,
                                   LogEventLevel inExpectedLevel,
                                   Throwable inException,
                                   I18NMessage inExpectedMessage,
                                   Serializable...inExpectedParameters)
        throws Exception
    {
        assertEquals(inExpectedLevel,
                     inActualEvent.getLevel());
        assertEquals(inException,
                     inActualEvent.getException());
        String messageText = inExpectedMessage.getMessageProvider().getText(inExpectedMessage,
                                                                            (Object[])inExpectedParameters);
        assertEquals(messageText,
                     inActualEvent.getMessage());
        // serialize event

        LogEvent serializedEvent = (LogEvent)
            SerializationUtils.deserialize
            (SerializationUtils.serialize(inActualEvent));
        assertEquals(inExpectedLevel,
                     serializedEvent.getLevel());
        if(inException == null) {
            assertNull(serializedEvent.getException());
        } else {
            assertEquals(inException.getMessage(),
                         serializedEvent.getException().getMessage());
        }
        assertEquals(messageText,
                     serializedEvent.getMessage());
    }
}
