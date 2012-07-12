package org.marketcetera.photon.strategy.engine.ui.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.commons.ui.IdentityComparer;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesContentProvider;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesLabelProvider;

/* $License$ */

/**
 * Utility for tests that need a tree of strategy engines using
 * {@link StrategyEnginesContentProvider}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyEngineTreeTestHelper {

    private final ApplicationWindow mWindow;
    private final WritableList mModel;
    private final StrategyEnginesContentProvider mContentProvider;
    private final IBaseLabelProvider mLabelProvider;
    private TreeViewer mViewer;

    public StrategyEngineTreeTestHelper() {
        checkThread();
        mModel = WritableList.withElementType(StrategyEngine.class);
        mContentProvider = new StrategyEnginesContentProvider();
        mLabelProvider = createLabelProvider(mContentProvider
                .getKnownElements());
        mWindow = new ApplicationWindow(null) {

            @Override
            protected Control createContents(Composite parent) {
                mViewer = new TreeViewer(parent);
                mViewer.setComparer(new IdentityComparer());
                mViewer.setContentProvider(mContentProvider);
                mViewer.setLabelProvider(mLabelProvider);
                mViewer.setInput(mModel);
                return mViewer.getTree();
            }
        };
    }

    protected IBaseLabelProvider createLabelProvider(IObservableSet elements) {
        checkThread();
        return StrategyEnginesLabelProvider.createAndTrack(elements);
    }

    public void openWindow() {
        checkThread();
        assertThat(mWindow.open(), is(Window.OK));
    }

    public void closeWindow() {
        checkThread();
        assertThat(mWindow.close(), is(true));
        mModel.dispose();
    }

    public WritableList getModel() {
        checkThread();
        return mModel;
    }

    public StrategyEnginesContentProvider getContentProvider() {
        checkThread();
        return mContentProvider;
    }

    public IBaseLabelProvider getLabelProvider() {
        checkThread();
        return mLabelProvider;
    }

    public TreeViewer getTreeViewer() {
        checkThread();
        return mViewer;
    }

    private void checkThread() {
        // thread confined
        assertThat(Display.getCurrent(), not(nullValue()));
    }
}
