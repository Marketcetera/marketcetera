package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;

/* $License$ */

/**
 * Tests {@link DeployedStrategyPropertyTester}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyEnginePropertyTesterTest {

    private StrategyEnginePropertyTester mFixture;

    @Before
    public void before() {
        mFixture = new StrategyEnginePropertyTester();
    }

    @Test
    public void testState() throws Exception {
        final StrategyEngine engine = createEngine("");
        assertThat(mFixture.test(engine, "connectionState", null, "CONNECTED"),
                is(false));
        assertThat(mFixture.test(engine, "connectionState", null,
                "DISCONNECTED"), is(true));
        engine.setConnectionState(ConnectionState.CONNECTED);
        assertThat(mFixture.test(engine, "connectionState", null, "CONNECTED"),
                is(true));
        assertThat(mFixture.test(engine, "connectionState", null,
                "DISCONNECTED"), is(false));
        new ExpectedIllegalArgumentException("def") {
            @Override
            protected void run() throws Exception {
                mFixture.test(engine, "def", null, "123");
            }
        };
        new ExpectedFailure<ClassCastException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.test(new Object(), "connectionState", null,
                        "CONNECTED");
            }
        };
    }
}
