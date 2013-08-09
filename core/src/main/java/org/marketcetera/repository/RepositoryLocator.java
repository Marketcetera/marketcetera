package org.marketcetera.repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RepositoryLocator<RepositoryType extends Repository>
{
    public RepositoryType locate();
}
