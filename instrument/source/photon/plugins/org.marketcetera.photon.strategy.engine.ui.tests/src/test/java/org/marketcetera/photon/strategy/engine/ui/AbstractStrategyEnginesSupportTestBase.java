package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ui.SWTUtilsTest.ExpectedThreadCheckFailure;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/* $License$ */

/**
 * Base class for {@link AbstractStrategyEnginesSupport} subclass tests.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public abstract class AbstractStrategyEnginesSupportTestBase extends
        PhotonTestBase {

    protected IStrategyEngines mRegisteredService;
    protected BundleContext mMockContext;
    protected ServiceRegistration mMockRegistration;

    @Before
    @UI
    public void before() {
        mMockContext = mock(BundleContext.class);
        mMockRegistration = mock(ServiceRegistration.class);
        when(
                mMockContext.registerService(eq(IStrategyEngines.class
                        .getName()), anyObject(), (Dictionary<?, ?>) isNull()))
                .thenAnswer(new Answer<ServiceRegistration>() {
                    @Override
                    public ServiceRegistration answer(
                            InvocationOnMock invocation) throws Throwable {
                        mRegisteredService = (IStrategyEngines) invocation
                                .getArguments()[1];
                        return mMockRegistration;
                    }
                });
    }

    @Test
    @UI
    public void testInitNull() throws Exception {
        new ExpectedNullArgumentFailure("context") {
            @Override
            protected void run() throws Exception {
                createAndInit(null);
            }
        };
    }

    @Test
    @UI
    public void testDispose() throws Exception {
        AbstractStrategyEnginesSupport fixture = createAndInit(mMockContext);
        IObservableList engines = mRegisteredService.getStrategyEngines();
        fixture.dispose();
        verify(mMockRegistration).unregister();
        assertTrue(engines.isDisposed());
        new ExpectedFailure<IllegalStateException>(
                "The strategy engines service is no longer available.") {
            @Override
            protected void run() throws Exception {
                mRegisteredService.getStrategyEngines();
            }
        };
        new ExpectedFailure<IllegalStateException>(
                "The strategy engines service is no longer available.") {
            @Override
            protected void run() throws Exception {
                mRegisteredService.addEngine(createEngine("ABCD"));
            }
        };
        new ExpectedFailure<IllegalStateException>(
                "The strategy engines service is no longer available.") {
            @Override
            protected void run() throws Exception {
                mRegisteredService.removeEngine(createEngine("ABCD"));
            }
        };
        /*
         * Should be okay to call dispose again.
         */
        reset(mMockRegistration);
        fixture.dispose();
        verify(mMockRegistration, never()).unregister();
    }

    abstract protected AbstractStrategyEnginesSupport createAndInit(
            BundleContext bundleContext);

    @Test
    @UI
    public void testUnmodifiableList() throws Exception {
        createAndInit(mMockContext);
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run() throws Exception {
                mRegisteredService.getStrategyEngines().clear();
            }
        };
    }

    @Test
    @UI
    public void testAdd() throws Exception {
        createAndInit(mMockContext);
        final int size = mRegisteredService.getStrategyEngines().size();
        final StrategyEngine engine = createEngineToAdd();
        final AtomicBoolean listenerCalled = new AtomicBoolean();
        mRegisteredService.getStrategyEngines().addListChangeListener(
                new IListChangeListener() {
                    @Override
                    public void handleListChange(ListChangeEvent event) {
                        listenerCalled.set(true);
                        final ListDiffEntry listDiffEntry = event.diff
                                .getDifferences()[0];
                        assertTrue(listDiffEntry.isAddition());
                        assertAdded(
                                (StrategyEngine) listDiffEntry.getElement(),
                                engine);
                        assertThat(listDiffEntry.getPosition(), is(size));
                    }
                });
        assertAdded(mRegisteredService.addEngine(engine), engine);
        assertAdded((StrategyEngine) mRegisteredService.getStrategyEngines()
                .get(size), engine);
        assertTrue(listenerCalled.get());
    }

    abstract protected void assertAdded(StrategyEngine returned,
            StrategyEngine added);

    abstract protected StrategyEngine createEngineToAdd();

    @Test
    @UI
    public void testAddNull() throws Exception {
        createAndInit(mMockContext);
        new ExpectedNullArgumentFailure("engine") {
            @Override
            protected void run() throws Exception {
                mRegisteredService.addEngine(null);
            }
        };
    }

    @Test
    @UI
    public void testRemove() throws Exception {
        createAndInit(mMockContext);
        final int size = mRegisteredService.getStrategyEngines().size();
        StrategyEngine engine = createEngineToAdd();
        final StrategyEngine returned = mRegisteredService.addEngine(engine);
        final AtomicBoolean listenerCalled = new AtomicBoolean();
        mRegisteredService.getStrategyEngines().addListChangeListener(
                new IListChangeListener() {
                    @Override
                    public void handleListChange(ListChangeEvent event) {
                        listenerCalled.set(true);
                        ListDiffEntry listDiffEntry = event.diff
                                .getDifferences()[0];
                        assertFalse(listDiffEntry.isAddition());
                        assertThat(listDiffEntry.getElement(),
                                sameInstance((Object) returned));
                        assertThat(listDiffEntry.getPosition(), is(size));
                    }
                });
        mRegisteredService.removeEngine(returned);
        assertThat(mRegisteredService.getStrategyEngines().size(), is(size));
        assertTrue(listenerCalled.get());
    }

    @Test
    @UI
    public void testRemoveNull() throws Exception {
        createAndInit(mMockContext);
        new ExpectedNullArgumentFailure("engine") {
            @Override
            protected void run() throws Exception {
                mRegisteredService.removeEngine(null);
            }
        };
    }

    @Test
    public void testInitWrongThread() throws Exception {
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                createAndInit(mMockContext);
            }
        };
    }

    @Test
    public void testAccessWrongThread() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                createAndInit(mMockContext);
            }
        });
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                mRegisteredService.getStrategyEngines();
            }
        };
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                mRegisteredService.addEngine(createEngine("ABCD"));
            }
        };
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                mRegisteredService.removeEngine(createEngine("ABCD"));
            }
        };
    }

    /**
     * 
     */
    public AbstractStrategyEnginesSupportTestBase() {
        super();
    }

}