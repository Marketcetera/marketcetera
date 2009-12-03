package org.marketcetera.photon.strategy.engine.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.marketcetera.photon.internal.strategy.engine.ui.DeployStrategyCompositeTest;
import org.marketcetera.photon.internal.strategy.engine.ui.DeployedStrategyConfigurationCompositeTest;
import org.marketcetera.photon.internal.strategy.engine.ui.StrategyEngineIdentificationCompositeTest;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizardTest;
import org.marketcetera.photon.strategy.engine.ui.EMFPropertyListenerCaveatTest;
import org.marketcetera.photon.strategy.engine.ui.ObservableListTreeContentProviderCaveatTest;
import org.marketcetera.photon.strategy.engine.ui.ScriptSelectionButtonTest;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColorsTest;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineStatusDecoratorTest;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineUITest;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesContentProviderTest;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesLabelProviderTest;
import org.marketcetera.photon.strategy.engine.ui.AbstractStrategyEnginesSupportTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(Suite.class)
@SuiteClasses( { StrategyEngineColorsTest.class,
        StrategyEnginesLabelProviderTest.class,
        StrategyEnginesContentProviderTest.class,
        ObservableListTreeContentProviderCaveatTest.class,
        EMFPropertyListenerCaveatTest.class,
        StrategyEngineStatusDecoratorTest.class,
        DeployStrategyCompositeTest.class, ScriptSelectionButtonTest.class,
        DeployStrategyWizardTest.class,
        StrategyEngineIdentificationCompositeTest.class,
        DeployedStrategyConfigurationCompositeTest.class,
        org.marketcetera.photon.strategy.engine.ui.MessagesTest.class,
        org.marketcetera.photon.internal.strategy.engine.ui.MessagesTest.class,
        StrategyEngineUITest.class, AbstractStrategyEnginesSupportTest.class })
public final class StrategyEngineUISuite {
}
