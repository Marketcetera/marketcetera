package org.marketcetera.persist;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MultiFruitQuery
        extends AbstractMultiEntityQuery<Fruit>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.MultiEntityQuery#getEntityType()
     */
    @Override
    protected Class<Fruit> getEntityType()
    {
        return Fruit.class;
    }
}
