package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;

/* $License$ */

/**
 * Tests {@link StrategyEngineAdapterFactory}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyEngineAdapterFactoryTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testAdapterList() {
        assertThat(new StrategyEngineAdapterFactory().getAdapterList(),
                hasItemInArray((Class) IWorkbenchAdapter.class));
    }

    @Test
    public void testAdapter() {
        StrategyEngine engine = createEngine("BogusEngine");
        StrategyEngineAdapterFactory fixture = new StrategyEngineAdapterFactory();
        IWorkbenchAdapter adapter = (IWorkbenchAdapter) fixture.getAdapter(
                engine, IWorkbenchAdapter.class);
        assertThat(adapter.getLabel(engine), is("BogusEngine"));
        assertThat(fixture.getAdapter(new Object(), IWorkbenchAdapter.class),
                nullValue());
        assertThat(fixture.getAdapter(engine, String.class), nullValue());
    }
}
