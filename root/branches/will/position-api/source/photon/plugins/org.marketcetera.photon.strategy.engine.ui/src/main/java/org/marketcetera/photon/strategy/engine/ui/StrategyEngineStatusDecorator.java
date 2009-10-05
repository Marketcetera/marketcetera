package org.marketcetera.photon.strategy.engine.ui;

import java.util.List;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.marketcetera.photon.commons.ui.SWTUtils;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher.IPropertiesChangedListener;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.model.core.util.StrategyEngineCoreSwitch;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors.StrategyEngineColor;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* $License$ */

/**
 * Decorates Strategy Engine objects based on their status. This is a
 * {@link ILightweightLabelDecorator} which is primarily intended to be used
 * declaratively with the Eclipse Workbench's DecorationManager for efficient
 * background decoration. However, it doesn't have any direct dependencies on
 * the workbench and could be used outside the workbench by creating a
 * {@link DecoratingLabelProvider} with an {@link ILabelDecorator} that
 * delegates to it.
 * <p>
 * {@link StrategyEngineColors} must be initialized for this class to function
 * properly.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEngineStatusDecorator extends BaseLabelProvider implements
        ILightweightLabelDecorator {

    /**
     * Creates a StrategyEngineStatusDecorator that tracks the given set of
     * elements, updating their labels when related properties change. Tracking
     * will not stop until the returned object is {@link #dispose() disposed}.
     * 
     * @param elements
     *            the observable set of elements to track, must be on the realm
     *            of the current display
     * @throws IllegalStateException
     *             if called from a non UI thread, i.e. a thread where
     *             Display.getCurrent() is null
     * @throws IllegalArgumentException
     *             if the provided elements set is not on the realm of the
     *             current display
     */
    public static StrategyEngineStatusDecorator createAndTrack(
            IObservableSet elements) {
        SWTUtils.checkThread();
        StrategyEngineStatusDecorator decorator = new StrategyEngineStatusDecorator();
        decorator.track(elements);
        return decorator;
    }

    /**
     * Properties this decorator cares about.
     */
    public final static List<IValueProperty> PROPERTIES = ImmutableList
            .<IValueProperty> of(
                    EMFProperties
                            .value(StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__CONNECTION_STATE),
                    EMFProperties
                            .value(StrategyEngineCorePackage.Literals.DEPLOYED_STRATEGY__STATE));

    private final PropertyWatcher mPropertyWatcher = new PropertyWatcher(
            PROPERTIES, new IPropertiesChangedListener() {
                @Override
                public void propertiesChanged(ImmutableSet<?> affectedElements) {
                    LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
                            StrategyEngineStatusDecorator.this,
                            affectedElements.toArray());
                    fireLabelProviderChanged(newEvent);
                }
            });

    /**
     * Causes this object to track the given set of elements, updating their
     * labels when related properties change. Tracking will not stop until this
     * object is {@link #dispose() disposed}.
     * 
     * @param elements
     *            the observable set of elements to track, must be on the realm
     *            of the current display
     * @throws IllegalStateException
     *             if called from a non UI thread, i.e. a thread where
     *             Display.getCurrent() is null
     * @throws IllegalArgumentException
     *             if the provided elements set is not on the realm of the
     *             current display
     */
    public final void track(IObservableSet elements) {
        SWTUtils.checkThread();
        mPropertyWatcher.watch(elements);
    }

    @Override
    public final void decorate(Object element, final IDecoration decoration) {
        /*
         * Warning: this method may not be called from the UI thread if using
         * the workbench decoration manager. In that case the decoration manager
         * is responsible for synchronization to ensure the thread confined
         * model is visible.
         */
        if (element instanceof EObject) {
            new StrategyEngineCoreSwitch<Boolean>() {
                @Override
                public Boolean caseStrategyEngine(StrategyEngine object) {
                    if (isConnected(object)) {
                        replaceImage(decoration,
                                StrategyEngineImage.ENGINE_CONNECTED_OBJ
                                        .getImageDescriptor());
                        // use default color for foreground
                    } else {
                        replaceImage(decoration,
                                StrategyEngineImage.ENGINE_DISCONNECTED_OBJ
                                        .getImageDescriptor());
                        decoration
                                .setForegroundColor(StrategyEngineColor.ENGINE_DISCONNECTED
                                        .getColor());
                    }
                    return Boolean.TRUE;
                }

                @Override
                public Boolean caseDeployedStrategy(DeployedStrategy object) {
                    if (isRunning(object)) {
                        replaceImage(decoration,
                                StrategyEngineImage.STRATEGY_RUNNING_OBJ
                                        .getImageDescriptor());
                        // use default color for foreground
                    } else {
                        replaceImage(decoration,
                                StrategyEngineImage.STRATEGY_STOPPED_OBJ
                                        .getImageDescriptor());
                        decoration
                                .setForegroundColor(StrategyEngineColor.STRATEGY_STOPPED
                                        .getColor());
                    }
                    return Boolean.TRUE;
                };
            }.doSwitch((EObject) element);
        }
    }

    private boolean isRunning(DeployedStrategy strategy) {
        return strategy.getState() == StrategyState.RUNNING;
    }

    private boolean isConnected(StrategyEngine engine) {
        return engine.getConnectionState() == ConnectionState.CONNECTED;
    }

    private void replaceImage(final IDecoration decoration,
            ImageDescriptor imageDescriptor) {
        /*
         * Without setting IDecoration.ENABLE_REPLACE, IDecoration.REPLACE will
         * not be respected
         */
        ((DecorationContext) decoration.getDecorationContext()).putProperty(
                IDecoration.ENABLE_REPLACE, Boolean.TRUE);
        decoration.addOverlay(imageDescriptor, IDecoration.REPLACE);
    }

    @Override
    public final void dispose() {
        mPropertyWatcher.dispose();
        super.dispose();
    }
}
