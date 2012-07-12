package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.StrategyEnginesSupportTest;
import org.marketcetera.photon.internal.strategy.StrategyTemplateTest;
import org.marketcetera.photon.internal.strategy.TradeSuggestionManagerTest;
import org.marketcetera.photon.internal.strategy.ui.NewJavaStrategyWizardTest;
import org.marketcetera.photon.internal.strategy.ui.NewRubyStrategyWizardTest;
import org.marketcetera.photon.internal.strategy.ui.UndeployDeleteParticipantTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
        org.marketcetera.photon.internal.strategy.MessagesTest.class,
        org.marketcetera.photon.internal.strategy.ui.MessagesTest.class,
        TradeSuggestionManagerTest.class, NewRubyStrategyWizardTest.class,
        NewJavaStrategyWizardTest.class, UndeployDeleteParticipantTest.class,
        StrategyEnginesSupportTest.class, StrategyTemplateTest.class })
public class StrategySuite {
}
