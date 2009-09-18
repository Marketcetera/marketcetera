package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertStrategy;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link EmbeddedConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EmbeddedConnectionPersistenceTest extends EmbeddedConnectionTest {

    private MockPersistenceService mPersistenceService;

    @Before
    public void before() throws Exception {
        super.before();
        mPersistenceService = new MockPersistenceService();
    }

    @Override
    public void testDeploy() throws Exception {
        super.testDeploy();
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat1",
                "MyStrategy", "RUBY", getTempScript().getPath(), false, null);
    }

    @Override
    public void testDeployWithParameters() throws Exception {
        super.testDeployWithParameters();
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat2",
                "MyStrategy2", "JAVA", getTempScript().getPath(), true,
                ImmutableMap.of("abc", "xyz", "123", "abc"));
    }

    @Override
    public void testUndeploy() throws Exception {
        super.testUndeploy();
        assertThat(mPersistenceService.getPersisted().size(), is(0));
    }

    @Override
    public void testUpdate() throws Exception {
        super.testUpdate();
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat1",
                "MyStrategy", "RUBY", getTempScript().getPath(), true,
                ImmutableMap.of("xyz", "123"));
    }

    @Override
    public void testRefreshDeployedStrategy() throws Exception {
        super.testRefreshDeployedStrategy();
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat1",
                "MyStrategy", "RUBY", getTempScript().getPath(), true,
                ImmutableMap.of("xyz", "123"));
    }

    @Override
    public void testRefreshDeployedStrategyThatNoLongerExists()
            throws Exception {
        super.testRefreshDeployedStrategyThatNoLongerExists();
        assertThat(mPersistenceService.getPersisted().size(), is(0));
    }

    @Override
    public void testRefresh() throws Exception {
        super.testRefresh();
        assertThat(mPersistenceService.getPersisted().size(), is(2));
        for (Strategy persisted : mPersistenceService.getPersisted()) {
            if (persisted.getInstanceName().equals("strat1")) {
                assertStrategy(mPersistenceService.getPersisted().get(0),
                        "strat1", "MyStrategy", "RUBY", getTempScript()
                                .getPath(), true, ImmutableMap.of("xyz", "123"));
            } else {
                assertStrategy(mPersistenceService.getPersisted().get(1),
                        "strat3", "MyStrategy", "RUBY", null, false, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveStateException() throws Exception {
        String category = EmbeddedConnection.class.getName();
        setLevel(category, Level.ERROR);
        IPersistenceService mockService = mock(IPersistenceService.class);
        EmbeddedConnection fixture = new EmbeddedConnection(mEngine,
                new ImmediateExecutorService(), mockService);
        fixture.initialize();
        doThrow(new IOException()).when(mockService).save(
                (List<? extends Strategy>) anyObject());
        fixture.deploy(createStrategyToDeploy());
        assertSingleEvent(Level.ERROR, category,
                "The embedded engine state could not be saved.", category);

    }

    @Override
    protected AbstractStrategyEngineConnection createFixture() {
        EmbeddedConnection fixture = new EmbeddedConnection(mEngine,
                new ImmediateExecutorService(), mPersistenceService);
        fixture.initialize();
        assertThat(mPersistenceService.isRestored(), is(true));
        return fixture;
    }

    private class MockPersistenceService implements IPersistenceService {

        private ImmutableList<Strategy> mPersisted;
        private boolean mRestored;

        @Override
        public void restore(StrategyEngineConnection connection) {
            mRestored = true;
        }

        @Override
        public void save(Collection<? extends Strategy> strategies)
                throws IOException {
            mPersisted = ImmutableList.copyOf(strategies);
        }

        public ImmutableList<Strategy> getPersisted() {
            return mPersisted;
        }

        public boolean isRestored() {
            return mRestored;
        }
    }
}
