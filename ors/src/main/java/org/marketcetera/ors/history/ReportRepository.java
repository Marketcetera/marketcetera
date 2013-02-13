package org.marketcetera.ors.history;

import org.marketcetera.persist.EntityRepository;

/* $License$ */

/**
 * Provides datastore access to <code>PersistentReport</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ReportRepository
        extends EntityRepository<PersistentReport>
{
}
