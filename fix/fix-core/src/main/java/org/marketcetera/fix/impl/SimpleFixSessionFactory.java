package org.marketcetera.fix.impl;

import java.util.Map;

import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
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
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixSessionFactory#create(java.util.Map)
     */
    @Override
    public SimpleFixSession create(Map<String,String> inAttributes)
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
