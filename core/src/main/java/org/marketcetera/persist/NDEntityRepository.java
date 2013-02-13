package org.marketcetera.persist;

/* $License$ */

/**
 * Provides datastore access to <code>NDEntityBase</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NDEntityRepository<Clazz extends NDEntityBase>
        extends EntityRepository<Clazz>
{
    /**
     * Finds the <code>Clazz</code> object with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>Clazz</code> value
     */
    public Clazz findByName(String inName);
}
