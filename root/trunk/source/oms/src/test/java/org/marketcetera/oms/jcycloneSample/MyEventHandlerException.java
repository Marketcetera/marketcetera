package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.handler.EventHandlerException;
import org.marketcetera.core.ClassVersion;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MyEventHandlerException extends EventHandlerException {
    public MyEventHandlerException(Exception nested) {
        super(nested.getMessage());
    }
}
