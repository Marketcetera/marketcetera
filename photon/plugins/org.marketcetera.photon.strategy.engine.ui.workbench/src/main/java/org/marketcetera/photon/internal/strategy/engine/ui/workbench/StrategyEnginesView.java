package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.marketcetera.photon.commons.osgi.HighestRankedTracker;
import org.marketcetera.photon.commons.osgi.HighestRankedTracker.IHighestRankedTrackerListener;
import org.marketcetera.photon.commons.ui.IdentityComparer;
import org.marketcetera.photon.commons.ui.workbench.WorkaroundGuiceExtensionFactory;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.inject.Inject;

/* $License$ */

/**
 * A view for strategy engines. It uses for input the {@link IStrategyEngines}
 * model service. It is {@link CommonNavigator} view, supporting dynamic
 * extensions.
 * <p>
 * This view requires a {@link BundleContext} to be injected and thus must be
 * created with the {@link WorkaroundGuiceExtensionFactory}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEnginesView extends CommonNavigator implements
        IStrategyEngines {

    /**
     * The id of the Eclipse properties command. Used to execute the command
     * programmatically.
     */
    private static final String PROPERTIES_COMMAND_ID = "org.eclipse.ui.file.properties"; //$NON-NLS-1$

    /**
     * Tracks the model.
     */
    private final ServiceTracker mEnginesTracker;

    /**
     * Input to the common viewer, confined to the UI thread.
     */
    private IObservableList mInput;

    /**
     * Indicates that the view has been disposed.
     */
    private volatile boolean mDisposed;

    /**
     * Caches the current service being used.
     */
    private volatile IStrategyEngines mService;

    /**
     * Caches the {@link IContextActivation} token that is used to deactivate
     * the context.
     */
    private IContextActivation mViewReadyContextToken;

    @Inject
    public StrategyEnginesView(BundleContext context) {
        mEnginesTracker = new HighestRankedTracker(context,
                IStrategyEngines.class.getName(),
                new IHighestRankedTrackerListener() {
                    @Override
                    public void highestRankedServiceChanged(Object newService) {
                        mService = (IStrategyEngines) newService;
                        if (mService != null) {
                            setInput(mService.getStrategyEngines());
                        } else {
                            setInput(null);
                        }
                    }
                });
    }

    @Override
    public void createPartControl(Composite parent) {
        // ensure colors are loaded
        StrategyEngineColors.init();
        super.createPartControl(parent);
        // required per StrategyEngineContentProvider
        getCommonViewer().setComparer(new IdentityComparer());
        getCommonViewer().setComparator(null);
        mEnginesTracker.open();
        hookContextMenu();
        getCommonViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IHandlerService service = (IHandlerService) getViewSite()
                        .getService(IHandlerService.class);
                try {
                    service.executeCommand(PROPERTIES_COMMAND_ID, null);
                } catch (Exception e) {
                    ExceptUtils
                            .swallow(
                                    e,
                                    StrategyEnginesView.this,
                                    Messages.STRATEGY_ENGINES_VIEW_FAILED_TO_OPEN_PROPERTIES);
                }
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        Menu menu = menuMgr.createContextMenu(getCommonViewer().getControl());
        getCommonViewer().getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, getCommonViewer());
    }

    @Override
    public void dispose() {
        mDisposed = true;
        mEnginesTracker.close();
        super.dispose();
        if (mInput != null) {
            mInput.dispose();
            mInput = null;
        }
    }

    @Override
    protected Object getInitialInput() {
        return null;
    }

    @Override
    public IObservableList getStrategyEngines() {
        return Observables.unmodifiableObservableList(mInput);
    }

    @Override
    public StrategyEngine addEngine(StrategyEngine engine) {
        if (mService != null) {
            return mService.addEngine(engine);
        }
        return null;
    }

    @Override
    public void removeEngine(StrategyEngine engine) {
        if (mService != null) {
            mService.removeEngine(engine);
        }
    }

    private void setInput(final IObservableList input) {
        getViewSite().getShell().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (mInput != null) {
                    mInput.dispose();
                    mInput = null;
                }
                if (!mDisposed) {
                    if (input == null) {
                        if (mViewReadyContextToken != null) {
                            mViewReadyContextToken.getContextService()
                                    .deactivateContext(mViewReadyContextToken);
                        }
                    } else {
                        mInput = Observables.unmodifiableObservableList(input);
                        mInput.addDisposeListener(new IDisposeListener() {
                            @Override
                            public void handleDispose(DisposeEvent staleEvent) {
                                setInput(null);
                            }
                        });
                        mViewReadyContextToken = ((IContextService) PlatformUI
                                .getWorkbench().getService(
                                        IContextService.class))
                                .activateContext(StrategyEngineWorkbenchUI.STRATEGY_ENGINES_VIEW_READY_CONTEXT_ID);
                    }
                    getCommonViewer().setInput(mInput);
                    getCommonViewer().expandAll();
                }
            }
        });
    }
}
