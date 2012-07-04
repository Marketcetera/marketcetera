package org.marketcetera.core.event.impl;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Indicates that the underlying event builder supports the attributes necessary to build future events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureEventBuilder.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ClassVersion("$Id: FutureEventBuilder.java 16063 2012-01-31 18:21:55Z colin $")
public interface FutureEventBuilder<B>
{
    /**
     * Sets the contract size.
     *
     * @param inContractSize an <code>int</code> value
     * @return a <code>B</code> value
     */
    public B withContractSize(int inContractSize);
}
