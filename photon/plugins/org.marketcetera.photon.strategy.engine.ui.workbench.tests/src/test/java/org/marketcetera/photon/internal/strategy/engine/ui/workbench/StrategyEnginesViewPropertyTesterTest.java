package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.mockito.Mockito.mock;

import org.eclipse.core.commands.Command;
import org.junit.Test;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;

/* $License$ */

/**
 * Tests {@link StrategyEnginesViewPropertyTester}.
 * 
 * Note: connectCommandHandled and disconnectCommandHandled properties are
 * tested in {@link StrategyEnginesViewTest} since {@link Command} cannot be
 * mocked.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyEnginesViewPropertyTesterTest {

    @Test
    public void testUnknownProperty() throws Exception {
        new ExpectedIllegalArgumentException("unknown property [arg]") {
            @Override
            protected void run() throws Exception {
                new StrategyEnginesViewPropertyTester().test(
                        mock(StrategyEnginesView.class), "arg", null, "123");
            }
        };
    }

}
