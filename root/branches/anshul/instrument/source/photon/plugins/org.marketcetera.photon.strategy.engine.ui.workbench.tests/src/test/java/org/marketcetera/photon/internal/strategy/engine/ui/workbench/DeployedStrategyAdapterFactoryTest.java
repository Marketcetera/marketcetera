package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;

/* $License$ */

/**
 * Tests {@link StrategyEngineAdapterFactory}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DeployedStrategyAdapterFactoryTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testAdapterList() {
        assertThat(new DeployedStrategyAdapterFactory().getAdapterList(),
                hasItemInArray((Class) IWorkbenchAdapter.class));
    }

    @Test
    public void testAdapter() {
        StrategyEngine engine = createEngine("BogusEngine");
        DeployedStrategy strategy = createDeployedStrategy("BogusStrategy");
        engine.getDeployedStrategies().add(strategy);
        DeployedStrategyAdapterFactory fixture = new DeployedStrategyAdapterFactory();
        IWorkbenchAdapter adapter = (IWorkbenchAdapter) fixture.getAdapter(
                strategy, IWorkbenchAdapter.class);
        assertThat(adapter.getLabel(strategy),
                is("BogusStrategy on BogusEngine"));
        assertThat(fixture.getAdapter(new Object(), IWorkbenchAdapter.class),
                nullValue());
        assertThat(fixture.getAdapter(strategy, String.class), nullValue());
    }

}
