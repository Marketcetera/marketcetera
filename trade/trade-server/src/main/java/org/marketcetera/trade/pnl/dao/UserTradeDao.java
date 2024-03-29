//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.dao;

import org.marketcetera.core.Preserve;
import org.marketcetera.trade.pnl.UserTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Providers data store access to {@link UserTrade} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface UserTradeDao
        extends JpaRepository<PersistentUserTrade,Long>,QuerydslPredicateExecutor<PersistentUserTrade>
{
}
