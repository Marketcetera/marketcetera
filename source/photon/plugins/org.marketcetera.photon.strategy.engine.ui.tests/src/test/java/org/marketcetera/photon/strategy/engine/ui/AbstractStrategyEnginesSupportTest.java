package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.DisplayThreadExecutor;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * Tests {@link AbstractStrategyEnginesSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class AbstractStrategyEnginesSupportTest extends
        AbstractStrategyEnginesSupportTestBase {

    @Override
    protected AbstractStrategyEnginesSupport createAndInit(
            BundleContext bundleContext) {
        return Fixture.createAndInit(bundleContext);
    }

    @Override
    protected StrategyEngine createEngineToAdd() {
        return createEngine("ABC");
    }

    @Override
    protected void assertAdded(StrategyEngine returned, StrategyEngine added) {
        assertThat(returned, sameInstance(added));
    }

    @Test
    @UI
    public void testInitialization() {
        final StrategyEngine engine = createEngine("ABC");
        new Fixture() {
            @Override
            protected void initList(List<StrategyEngine> engines) {
                assertThat(getGuiExecutor(), is(DisplayThreadExecutor
                        .getInstance(Display.getCurrent())));
                engines.add(engine);
            }
        }.init(mMockContext);
        assertThat(mRegisteredService.getStrategyEngines().get(0),
                sameInstance((Object) engine));
    }

    protected static class Fixture extends AbstractStrategyEnginesSupport {

        public static Fixture createAndInit(BundleContext context) {
            Fixture fixture = new Fixture();
            fixture.init(context);
            return fixture;
        }

        @Override
        protected void doRemoveEngine(List<StrategyEngine> engines,
                StrategyEngine engine) {
            engines.remove(engine);
        }

        @Override
        protected StrategyEngine doAddEngine(List<StrategyEngine> engines,
                StrategyEngine engine) {
            engines.add(engine);
            return engine;
        }

    }
}
