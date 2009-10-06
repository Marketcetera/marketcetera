package org.marketcetera.photon.strategy.engine.ui.workbench.resources.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.resources.WorkspaceScriptSelectionButtonTest;
import org.marketcetera.photon.strategy.engine.ui.workbench.resources.StrategyEngineWorkspaceUITest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
        WorkspaceScriptSelectionButtonTest.class,
        org.marketcetera.photon.internal.strategy.engine.ui.workbench.resources.MessagesTest.class,
        org.marketcetera.photon.strategy.engine.ui.workbench.resources.MessagesTest.class,
        StrategyEngineWorkspaceUITest.class })
public final class StrategyEngineWorkspaceSuite {
}
