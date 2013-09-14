package org.marketcetera.photon.strategy.engine.model.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnectionTest;
import org.marketcetera.photon.strategy.engine.StrategyEnginesCoreTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { StrategyEnginesCoreTest.class,
        AbstractStrategyEngineConnectionTest.class,
        org.marketcetera.photon.strategy.engine.MessagesTest.class })
public class StrategyEngineCoreSuite {
}
