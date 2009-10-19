package org.marketcetera.photon.strategy.engine.sa.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil.assertStrategyAgentEngine;
import static org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil.createStrategyAgentEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEnginesSupport;
import org.marketcetera.photon.test.EMFTestUtil;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link StrategyAgentEnginesSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyAgentEnginesSupportPersistenceTest extends
        StrategyAgentEnginesSupportTest {

    private IEMFPersistence mMockPersistence;
    private List<? extends EObject> mToRestore = Collections.emptyList();
    private List<? extends EObject> mSaved = Collections.emptyList();

    @Override
    public void before() {
        super.before();
        mMockPersistence = new IEMFPersistence() {
            @Override
            public void save(Collection<? extends EObject> objects)
                    throws IOException {
                mSaved = Lists.newArrayList(objects);
            }

            @Override
            public List<? extends EObject> restore() throws IOException {
                return mToRestore;
            }
        };
    }

    @Override
    protected StrategyAgentEnginesSupport createAndInit(
            BundleContext bundleContext) {
        return new StrategyAgentEnginesSupport(bundleContext, mMockPersistence);
    }

    protected StrategyAgentEnginesSupport createAndInitThrowingIOException(
            BundleContext bundleContext) {
        return new StrategyAgentEnginesSupport(mMockContext,
                new IEMFPersistence() {
                    @Override
                    public void save(Collection<? extends EObject> objects)
                            throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public List<? extends EObject> restore() throws IOException {
                        throw new IOException();
                    }
                });
    }

    protected StrategyAgentEnginesSupport createAndInitThrowingFileNotFoundException(
            BundleContext bundleContext) {
        return new StrategyAgentEnginesSupport(mMockContext,
                new IEMFPersistence() {
                    @Override
                    public void save(Collection<? extends EObject> objects)
                            throws IOException {
                    }

                    @Override
                    public List<? extends EObject> restore() throws IOException {
                        throw new FileNotFoundException();
                    }
                });
    }

    protected void assertSaved(StrategyAgentEngine engine) {
        assertThat(mSaved.size(), is(1));
        assertStrategyAgentEngine((StrategyAgentEngine) mSaved.get(0), engine);
    }

    protected void assertNothingSaved() {
        assertThat(mSaved.size(), is(0));
    }

    protected void prepareRestore(List<? extends EObject> toRestore) {
        mToRestore = toRestore;
    }

    protected StrategyAgentEngine assertRestored(StrategyAgentEngine engine) {
        StrategyAgentEngine restored = (StrategyAgentEngine) mRegisteredService
                .getStrategyEngines().get(0);
        assertStrategyAgentEngine(restored, engine);
        return restored;
    }

    @Override
    public void testAdd() throws Exception {
        super.testAdd();
        assertSaved(createEngineToAdd());
    }

    @Override
    public void testRemove() throws Exception {
        super.testRemove();
        assertNothingSaved();
    }

    @Test
    @UI
    public void testRestore() {
        StrategyAgentEngine engine = createEngineToAdd();
        prepareRestore(Collections.singletonList(engine));
        createAndInit(mMockContext);
        assertRestored(engine);
    }

    @Test
    @UI
    public void testUpdateAfterRestore() {
        StrategyAgentEngine engine = createEngineToAdd();
        prepareRestore(Collections.singletonList(engine));
        createAndInit(mMockContext);
        StrategyAgentEngine toUpdate = assertRestored(engine);
        toUpdate.setName("NewName");
        toUpdate.setDescription("NewDescr");
        toUpdate.setJmsUrl("NewUrl");
        toUpdate.setWebServiceHostname("NewHost");
        toUpdate.setWebServicePort(5);
        assertSaved(createStrategyAgentEngine("NewName", "NewDescr", "NewUrl",
                "NewHost", 5));
    }

    @Test
    @UI
    public void testUpdateAfterAddition() {
        createAndInit(mMockContext);
        StrategyAgentEngine engine = createEngineToAdd();
        StrategyAgentEngine toUpdate = (StrategyAgentEngine) mRegisteredService
                .addEngine(engine);
        toUpdate.setName("NewName");
        toUpdate.setDescription("NewDescr");
        toUpdate.setJmsUrl("NewUrl");
        toUpdate.setWebServiceHostname("NewHost");
        toUpdate.setWebServicePort(5);
        assertSaved(createStrategyAgentEngine("NewName", "NewDescr", "NewUrl",
                "NewHost", 5));
    }

    @Test
    @UI
    public void testTouchAndStrategiesIgnored() {
        StrategyAgentEngine engine = createEngineToAdd();
        prepareRestore(Collections.singletonList(engine));
        createAndInit(mMockContext);
        StrategyAgentEngine toUpdate = assertRestored(engine);
        // changes to deployed strategies are not tracked
        toUpdate.getDeployedStrategies().add(createDeployedStrategy("null"));
        assertNothingSaved();
        // nothing changed
        toUpdate.setName("A");
        assertNothingSaved();
        // now make a change
        toUpdate.setName("B");
        // assert the change (need to set new name on engine for assertion)
        engine.setName("B");
        assertSaved(engine);
    }

    @Test
    @UI
    public void testUnexpectedObjectDuringRestore() {
        String category = StrategyAgentEnginesSupport.class.getName();
        setLevel(category, Level.WARN);
        EObject unexpected = EMFTestUtil.createDynamicEObject();
        StrategyAgentEngine engine = createEngineToAdd();
        prepareRestore(Arrays.asList(unexpected, engine));
        createAndInit(mMockContext);
        assertRestored(engine);
        assertSingleEvent(Level.WARN, category,
                "Ignoring persisted object because it is not an expected type: "
                        + unexpected, null);
    }

    @Test
    @UI
    public void testIOExceptionDuringSaveAndRestore() {
        String category = StrategyAgentEnginesSupport.class.getName();
        setLevel(category, Level.ERROR);
        createAndInitThrowingIOException(mMockContext);
        assertLastEvent(
                Level.ERROR,
                category,
                "Failed to restore persisted engines. See underlying cause for details.",
                null);
        mRegisteredService.addEngine(createStrategyAgentEngine("ABC"));
        assertLastEvent(Level.ERROR, category,
                "Failed to save engines. See underlying cause for details.",
                null);
    }

    @Test
    @UI
    public void testFileNotFoundExceptionDuringRestore() {
        setLevel(StrategyAgentEnginesSupport.class.getName(), Level.DEBUG);
        createAndInitThrowingFileNotFoundException(mMockContext);
        assertLastEvent(Level.DEBUG, null,
                "No persisted engine restored because the file does not exist",
                null);
    }
}
