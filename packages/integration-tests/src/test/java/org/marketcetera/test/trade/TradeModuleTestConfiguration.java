package org.marketcetera.test.trade;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/* $License$ */

/**
 * Provides test configuration for trade server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TradeModuleTestConfiguration
        extends TradeServerTestConfiguration
{
}
