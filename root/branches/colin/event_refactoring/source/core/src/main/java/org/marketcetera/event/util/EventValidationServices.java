package org.marketcetera.event.util;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides event validation services. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id$")
public abstract class EventValidationServices
{
    /**
     * Causes an unchecked exception of the appropriate type
     * and message to be thrown. 
     *
     * @param inErrorMessage an <code>I18NBoundMessage</code> value
     */
    public static void error(I18NBoundMessage inErrorMessage)
    {
        throw new IllegalArgumentException(inErrorMessage.getText());
    }
}
