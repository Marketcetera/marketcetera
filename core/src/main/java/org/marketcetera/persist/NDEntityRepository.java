package org.marketcetera.persist;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NDEntityRepository<Clazz extends NDEntityBase>
        extends EntityRepository<Clazz>
{
    public Clazz findByName(String inName);
}
