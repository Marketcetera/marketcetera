package com.marketcetera.admin;

import org.marketcetera.trade.MarketceteraTestBase;
import org.springframework.boot.test.context.SpringBootTest;

/* $License$ */

/**
 * Provides common test behavior for admin server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootTest(classes=AdminTestConfiguration.class)
public class AdminTestBase
        extends MarketceteraTestBase
{
}
