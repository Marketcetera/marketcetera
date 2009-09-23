package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;

/* $License$ */

/**
 * Tests {@link DeployedStrategyPropertyTester}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DeployedStrategyPropertyTesterTest {

    private DeployedStrategyPropertyTester mFixture;

    @Before
    public void before() {
        mFixture = new DeployedStrategyPropertyTester();
    }

    @Test
    public void testState() throws Exception {
        final DeployedStrategy strategy = createDeployedStrategy("");
        assertThat(mFixture.test(strategy, "state", null, "RUNNING"), is(false));
        assertThat(mFixture.test(strategy, "state", null, "STOPPED"), is(true));
        strategy.setState(StrategyState.RUNNING);
        assertThat(mFixture.test(strategy, "state", null, "RUNNING"), is(true));
        assertThat(mFixture.test(strategy, "state", null, "STOPPED"), is(false));
        new ExpectedIllegalArgumentException("abc") {
            @Override
            protected void run() throws Exception {
                mFixture.test(strategy, "abc", null, "xyz");
            }
        };
        new ExpectedFailure<ClassCastException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.test(new Object(), "state", null, "xyz");
            }
        };
    }
}
