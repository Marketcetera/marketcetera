package org.marketcetera.fix;

/* $License$ */

/**
 * Creates {@link MutableActiveFixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableActiveFixSessionFactory
        extends ActiveFixSessionFactory
{
    /**
     * Create a <code>MutableActiveFixSession</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>MutableActiveFixSession</code> value
     */
    @Override
    MutableActiveFixSession create(ActiveFixSession inFixSession);
    /**
     * Create a <code>MutableActiveFixSession</code> object.
     *
     * @return a <code>MutableActiveFixSession</code> value
     */
    MutableActiveFixSession create();
}
