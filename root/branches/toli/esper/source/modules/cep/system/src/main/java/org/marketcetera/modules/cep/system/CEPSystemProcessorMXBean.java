package org.marketcetera.modules.cep.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

import javax.management.MXBean;

/* $License$ */
/**
 * The management interface for CEP system module instances.
 *
 * @author toli@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@MXBean(true)
@DisplayName("The Management Interface for Esper Module Instance")
public interface CEPSystemProcessorMXBean {
    /**
     * Returns the number of events emitted over the lifetime of the
     * event stream processing runtime.
     *
     * @return number of events emitted.
     */
    @DisplayName("The number of events emitted")
    long getNumEventsEmitted();

    /**
     * Returns the number of events received over the lifetime of the
     * event stream processing runtime.
     *
     * @return number of events received.
     */
    @DisplayName("The number of events received")
    long getNumEventsReceived();
}