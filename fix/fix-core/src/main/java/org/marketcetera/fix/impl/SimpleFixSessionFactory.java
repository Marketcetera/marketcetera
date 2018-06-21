package org.marketcetera.fix.impl;

import java.util.Map;

import org.marketcetera.fix.FixCoreUtil;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;

/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionFactory
        implements FixSessionFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSessionFactory#create()
     */
    @Override
    public SimpleFixSession create()
    {
        return new SimpleFixSession();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSessionFactory#create(org.marketcetera.fix.FixSession)
     */
    @Override
    public SimpleFixSession create(FixSession inFixSession)
    {
        return new SimpleFixSession(inFixSession);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSessionFactory#create(java.util.Map)
     */
    @Override
    public SimpleFixSession create(Map<String,String> inAttributes)
    {
        SimpleFixSession simpleFixSession = new SimpleFixSession();
        FixCoreUtil.applyFixSettings(simpleFixSession,
                                     inAttributes);
        return simpleFixSession;
    }
}
