package org.marketcetera.fix;

/* $License$ */

/**
 * Creates {@link ActiveFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ActiveFixSessionFactory
{
    /**
     * Create an <code>ActiveFixSession</code> object.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     * @return an <code>ActiveFixSession</code> value
     */
    ActiveFixSession create(ActiveFixSession inFixSession);
}
