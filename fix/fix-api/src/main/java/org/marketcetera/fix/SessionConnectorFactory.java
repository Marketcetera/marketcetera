package org.marketcetera.fix;


/* $License$ */

/**
 * Creates {@link SessionConnector} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionConnectorFactory
{
    /**
     * Creates a <code>SessionConnector</code> object.
     *
     * @param inSession a <code>FixSession</code> value
     * @return a <code>SessionConnector</code> value
     */
    SessionConnector create(FixSession inSession);
}
