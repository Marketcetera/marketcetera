package org.marketcetera.photon.internal.strategy;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.pass;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.strategyagent.tests.StrategyAgentEngineTestUtil.assertStrategyAgentEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.StrategyAgentEnginesSupport;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.StrategyAgentEnginesSupportPersistenceTest;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link StrategyEnginesSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEnginesSupportTest extends
        StrategyAgentEnginesSupportPersistenceTest {

    private File mFile = Platform.getStateLocation(
            Platform.getBundle(StrategyUI.PLUGIN_ID)).append("engines.xml")
            .toFile();
    private Resource mResource = new XMIResourceImpl(URI.createFileURI(mFile
            .getPath()));

    @Override
    public void before() {
        super.before();
        mFile.delete();
    }

    @Override
    protected void assertSaved(StrategyAgentEngine engine) {
        List<? extends EObject> saved = getSaved();
        assertThat(saved.size(), is(1));
        assertStrategyAgentEngine((StrategyAgentEngine) saved.get(0), engine);
    }

    @Override
    protected void assertNothingSaved() {
        assertThat(getSaved().size(), is(0));
    }

    @Override
    protected void prepareRestore(List<? extends EObject> toRestore) {
        try {
            mResource.getContents().clear();
            for (EObject object : toRestore) {
                mResource.getContents().add(object);
            }
            mResource.save(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StrategyAgentEngine assertRestored(StrategyAgentEngine engine) {
        StrategyAgentEngine restored = (StrategyAgentEngine) mRegisteredService
                .getStrategyEngines().get(1);
        assertStrategyAgentEngine(restored, engine);
        // reset
        prepareRestore(Collections.<EObject> emptyList());
        return restored;
    }

    private List<? extends EObject> getSaved() {
        try {
            // clear cache
            mResource.unload();
            mResource.load(null);
            List<EObject> list = Lists.newArrayList();
            for (EObject object : mResource.getContents()) {
                list.add(object);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StrategyEnginesSupport createAndInit(BundleContext bundleContext) {
        return new StrategyEnginesSupport(bundleContext);
    }

    protected StrategyEnginesSupport createAndInitThrowingIOException(
            BundleContext bundleContext) {
        return createAndInit(bundleContext);
    }

    @Override
    protected StrategyAgentEnginesSupport createAndInitThrowingFileNotFoundException(
            BundleContext bundleContext) {
        return createAndInit(bundleContext);
    }

    @Override
    public void testFileNotFoundExceptionDuringRestore() {
        setLevel(StrategyEnginesSupport.class.getName(), Level.DEBUG);
        super.testFileNotFoundExceptionDuringRestore();
    }

    @Override
    public void testUnexpectedObjectDuringRestore() {
        // skip for this subclass - hard to mock with real persistence
        pass();
    }

    @Override
    public void testIOExceptionDuringSaveAndRestore() {
        // skip for this subclass - hard to mock with real persistence
        pass();
    }

}
