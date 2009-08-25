package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.SWTUtilsTest.ExpectedThreadCheckFailure;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcherTest.PropertyWatcherElementsRealmCheckFailure;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors.StrategyEngineColor;
import org.marketcetera.photon.strategy.engine.ui.tests.StrategyEngineTreeTestHelper;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.JFaceAsserts;
import org.marketcetera.photon.test.LockRealm;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test {@link StrategyEngineStatusDecorator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyEngineStatusDecoratorTest extends PhotonTestBase {

    private volatile StrategyEngineStatusDecorator mFixture;
    private IDecoration mMockDecoration;
    private DecorationContext mMockDecorationContext;

    @Before
    public void before() throws Throwable {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                StrategyEngineColors.init();
            }
        });
        mFixture = new StrategyEngineStatusDecorator();
        mMockDecoration = mock(IDecoration.class);
        mMockDecorationContext = mock(DecorationContext.class);
        when(mMockDecoration.getDecorationContext()).thenReturn(
                mMockDecorationContext);
    }

    @After
    @UI
    public void after() {
        mFixture.dispose();
        StrategyEngineColors.dispose();
    }

    @Test
    public void testInvalidThread() throws Exception {
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                mFixture.track(new WritableSet(new LockRealm()));
            }
        };
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                StrategyEngineStatusDecorator.createAndTrack(new WritableSet(
                        new LockRealm()));
            }
        };
    }

    @Test
    @UI
    public void testElementsValidation() throws Exception {
        final LockRealm realm = new LockRealm();
        new PropertyWatcherElementsRealmCheckFailure(realm, Realm.getDefault()) {
            @Override
            protected void run() throws Exception {
                mFixture.track(new WritableSet(realm));
            }
        };
        new PropertyWatcherElementsRealmCheckFailure(realm, Realm.getDefault()) {
            @Override
            protected void run() throws Exception {
                StrategyEngineStatusDecorator.createAndTrack(new WritableSet(
                        realm));
            }
        };
    }

    @Test
    public void testDisconnectedEngine() {
        StrategyEngine engine = createEngine("");
        mFixture.decorate(engine, mMockDecoration);
        verify(mMockDecorationContext).putProperty(IDecoration.ENABLE_REPLACE,
                Boolean.TRUE);
        verify(mMockDecoration).addOverlay(
                StrategyEngineImage.ENGINE_DISCONNECTED_OBJ
                        .getImageDescriptor(), IDecoration.REPLACE);
        verify(mMockDecoration).setForegroundColor(
                StrategyEngineColor.ENGINE_DISCONNECTED.getColor());
    }

    @Test
    public void testConnectedEngine() {
        StrategyEngine engine = createEngine("");
        engine.setConnectionState(ConnectionState.CONNECTED);
        mFixture.decorate(engine, mMockDecoration);
        verify(mMockDecorationContext).putProperty(IDecoration.ENABLE_REPLACE,
                Boolean.TRUE);
        verify(mMockDecoration).addOverlay(
                StrategyEngineImage.ENGINE_CONNECTED_OBJ.getImageDescriptor(),
                IDecoration.REPLACE);
        verify(mMockDecoration, never())
                .setForegroundColor((Color) anyObject());
    }

    @Test
    public void testStoppedStrategy() {
        DeployedStrategy strategy = createDeployedStrategy("");
        mFixture.decorate(strategy, mMockDecoration);
        verify(mMockDecorationContext).putProperty(IDecoration.ENABLE_REPLACE,
                Boolean.TRUE);
        verify(mMockDecoration).addOverlay(
                StrategyEngineImage.STRATEGY_STOPPED_OBJ.getImageDescriptor(),
                IDecoration.REPLACE);
        verify(mMockDecoration).setForegroundColor(
                StrategyEngineColor.STRATEGY_STOPPED.getColor());
    }

    @Test
    public void testRunningStrategy() {
        DeployedStrategy strategy = createDeployedStrategy("");
        strategy.setState(StrategyState.RUNNING);
        mFixture.decorate(strategy, mMockDecoration);
        verify(mMockDecorationContext).putProperty(IDecoration.ENABLE_REPLACE,
                Boolean.TRUE);
        verify(mMockDecoration).addOverlay(
                StrategyEngineImage.STRATEGY_RUNNING_OBJ.getImageDescriptor(),
                IDecoration.REPLACE);
        verify(mMockDecoration, never())
                .setForegroundColor((Color) anyObject());
    }

    private StrategyEngineTreeTestHelper mHelper;
    private StrategyEngine mEngine1;
    private StrategyEngine mEngine2;
    private DeployedStrategy mStrategy;

    @Test
    public void testInViewer() throws Throwable {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mHelper = new StrategyEngineTreeTestHelper() {
                    @Override
                    protected IBaseLabelProvider createLabelProvider(
                            IObservableSet elements) {
                        final StrategyEnginesLabelProvider provider = new StrategyEnginesLabelProvider();
                        provider.track(elements);
                        return new DecoratingLabelProvider(provider,
                                new ProxyLabelDecorator(
                                        StrategyEngineStatusDecorator
                                                .createAndTrack(elements)));
                    }
                };
                mHelper.openWindow();
                mEngine1 = createEngine("ABC");
                mEngine2 = createEngine("DEF");
                mEngine2.setConnectionState(ConnectionState.CONNECTED);
                mStrategy = createDeployedStrategy("ABC");
                mHelper.getModel().addAll(
                        buildEngines(mEngine1, mStrategy, mEngine2));
            }
        });
        try {
            SWTBot bot = new SWTBot();
            final SWTBotTree tree = bot.tree();
            SWTBotTreeItem[] items = tree.getAllItems();
            assertForeground(items[0], StrategyEngineColor.ENGINE_DISCONNECTED);
            assertImage(items[0], StrategyEngineImage.ENGINE_DISCONNECTED_OBJ);
            assertDefaultForeground(items[1]);
            assertImage(items[1], StrategyEngineImage.ENGINE_CONNECTED_OBJ);
            items[0].expand();
            SWTBotTreeItem[] children = items[0].getItems();
            assertForeground(children[0], StrategyEngineColor.STRATEGY_STOPPED);
            assertImage(children[0], StrategyEngineImage.STRATEGY_STOPPED_OBJ);

            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    mEngine1.setConnectionState(ConnectionState.CONNECTED);
                    mEngine2.setConnectionState(ConnectionState.DISCONNECTED);
                    mStrategy.setState(StrategyState.RUNNING);
                }
            });
            assertDefaultForeground(items[0]);
            assertImage(items[0], StrategyEngineImage.ENGINE_CONNECTED_OBJ);
            assertForeground(items[1], StrategyEngineColor.ENGINE_DISCONNECTED);
            assertImage(items[1], StrategyEngineImage.ENGINE_DISCONNECTED_OBJ);
            assertDefaultForeground(children[0]);
            assertImage(children[0], StrategyEngineImage.STRATEGY_RUNNING_OBJ);
        } finally {
            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    mHelper.closeWindow();
                }
            });
        }
    }

    private void assertImage(final SWTBotTreeItem item,
            final StrategyEngineImage image) throws Throwable {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                JFaceAsserts.assertImage(item.widget.getImage(), image
                        .getImageDescriptor());
            }
        });
    }

    private void assertDefaultForeground(final SWTBotTreeItem item)
            throws Throwable {
        assertThat(getForeground(item), is(getSystemColor(SWT.COLOR_BLACK)));
    }

    private void assertForeground(final SWTBotTreeItem item,
            final StrategyEngineColor color) throws Throwable {
        assertThat(getForeground(item), is(getColor(color)));
    }

    private Color getForeground(final SWTBotTreeItem item) throws Throwable {
        return AbstractUIRunner.syncCall(new Callable<Color>() {
            @Override
            public Color call() throws Exception {
                return item.widget.getForeground(0);
            }
        });
    }

    private Color getColor(final StrategyEngineColor color) throws Throwable {
        return AbstractUIRunner.syncCall(new Callable<Color>() {
            @Override
            public Color call() throws Exception {
                return color.getColor();
            }
        });
    }

    private Color getSystemColor(final int color) throws Throwable {
        return AbstractUIRunner.syncCall(new Callable<Color>() {
            @Override
            public Color call() throws Exception {
                return Display.getCurrent().getSystemColor(color);
            }
        });
    }

    /**
     * Adapts an {@link ILightweightLabelDecorator} to an
     * {@link ILabelDecorator} for this test since the workbench isn't
     * available.
     */
    private static class ProxyLabelDecorator extends BaseLabelProvider
            implements ILabelDecorator, IColorDecorator, ILabelProviderListener {

        private final ILightweightLabelDecorator mLightweightDelegate;
        private final ResourceManager mResourceManager = new LocalResourceManager(
                JFaceResources.getResources());

        public ProxyLabelDecorator(
                ILightweightLabelDecorator lightweightDelegate) {
            mLightweightDelegate = lightweightDelegate;
            mLightweightDelegate.addListener(this);
        }

        @Override
        public Image decorateImage(Image image, Object element) {
            final ImageDescriptor descriptor = decorate(element)
                    .getImageDescriptor();
            return descriptor == null ? null : mResourceManager
                    .createImage(descriptor);
        }

        @Override
        public String decorateText(String text, Object element) {
            return null;
        }

        @Override
        public Color decorateBackground(Object element) {
            return null;
        }

        @Override
        public Color decorateForeground(Object element) {
            return decorate(element).getForegroundColor();
        }

        @Override
        public void dispose() {
            mResourceManager.dispose();
            super.dispose();
        }

        private Decoration decorate(Object element) {
            Decoration decoration = new Decoration();
            mLightweightDelegate.decorate(element, decoration);
            return decoration;
        }

        @Override
        public void labelProviderChanged(LabelProviderChangedEvent event) {
            fireLabelProviderChanged(event);
        }

        private static class Decoration implements IDecoration {

            private ImageDescriptor mImageDescriptor;
            private Color mForegroundColor;

            public ImageDescriptor getImageDescriptor() {
                return mImageDescriptor;
            }

            public Color getForegroundColor() {
                return mForegroundColor;
            }

            @Override
            public void addOverlay(ImageDescriptor overlay) {
            }

            @Override
            public void addOverlay(ImageDescriptor overlay, int quadrant) {
                if (quadrant == IDecoration.REPLACE) {
                    mImageDescriptor = overlay;
                }
            }

            @Override
            public void addPrefix(String prefix) {
            }

            @Override
            public void addSuffix(String suffix) {
            }

            @Override
            public IDecorationContext getDecorationContext() {
                return new DecorationContext();
            }

            @Override
            public void setBackgroundColor(Color color) {
            }

            @Override
            public void setFont(Font font) {
            }

            @Override
            public void setForegroundColor(Color color) {
                mForegroundColor = color;
            }

        }

    }

}
