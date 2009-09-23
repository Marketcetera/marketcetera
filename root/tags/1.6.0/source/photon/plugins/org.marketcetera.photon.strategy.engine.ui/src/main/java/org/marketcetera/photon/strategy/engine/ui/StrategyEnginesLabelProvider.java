package org.marketcetera.photon.strategy.engine.ui;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.commons.ui.SWTUtils;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher.IPropertiesChangedListener;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.model.core.util.StrategyEngineCoreSwitch;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* $License$ */

/**
 * Provides a basic label and image for strategy engine core model objects. It
 * implements {@link IStyledLabelProvider} so it may be used in viewers with
 * styled labels.
 * <p>
 * Instances of this class are thread confined. They can only be instantiated
 * and accessed on a single UI thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEnginesLabelProvider extends LabelProvider implements
        IStyledLabelProvider {

    /**
     * Creates a StrategyEnginesLabelProvider that tracks the given set of
     * elements, updating their labels when related properties change. Tracking
     * will not stop until this StrategyEnginesLabelProvider is
     * {@link #dispose() disposed}.
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
    public static StrategyEnginesLabelProvider createAndTrack(
            IObservableSet elements) {
        SWTUtils.checkThread();
        StrategyEnginesLabelProvider provider = new StrategyEnginesLabelProvider();
        provider.track(elements);
        return provider;
    }

    private final static List<IValueProperty> PROPERTIES = ImmutableList
            .<IValueProperty> of(
                    EMFProperties
                            .value(StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__NAME),
                    EMFProperties
                            .value(StrategyEngineCorePackage.Literals.STRATEGY__INSTANCE_NAME));

    private final PropertyWatcher mPropertyWatcher = new PropertyWatcher(
            PROPERTIES, new IPropertiesChangedListener() {
                @Override
                public void propertiesChanged(ImmutableSet<?> affectedElements) {
                    LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
                            StrategyEnginesLabelProvider.this, affectedElements
                                    .toArray());
                    fireLabelProviderChanged(newEvent);
                }
            });

    private final LocalResourceManager mResourceManager;

    /**
     * Constructor. Must be called from the UI thread.
     * 
     * @throws IllegalStateException
     *             if called from a non UI thread, i.e. a thread where
     *             Display.getCurrent() is null
     */
    public StrategyEnginesLabelProvider() {
        SWTUtils.checkThread();
        mResourceManager = new LocalResourceManager(JFaceResources
                .getResources());
    }

    /**
     * Causes this label provider to track the given set of elements, updating
     * their labels when related properties change. Tracking will not stop until
     * this StrategyEnginesLabelProvider is {@link #dispose() disposed}.
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
    public void track(IObservableSet elements) {
        SWTUtils.checkThread();
        final Realm realm = elements.getRealm();
        if (!realm.isCurrent()) {
            throw new IllegalArgumentException(
                    MessageFormat
                            .format(
                                    "the realm of elements [{0}] is not the realm of the current display", //$NON-NLS-1$
                                    realm));
        }
        mPropertyWatcher.watch(elements);
    }

    @Override
    public String getText(Object element) {
        SWTUtils.checkThread();
        if (element instanceof EObject) {
            String text = new StrategyEngineCoreSwitch<String>() {
                @Override
                public String caseStrategyEngine(StrategyEngine object) {
                    return object.getName();
                }

                @Override
                public String caseStrategy(Strategy object) {
                    return object.getInstanceName();
                };
            }.doSwitch((EObject) element);
            if (text != null) {
                return text;
            }
        }
        return super.getText(element);
    }

    @Override
    public StyledString getStyledText(Object element) {
        SWTUtils.checkThread();
        return new StyledString(getText(element));
    }

    @Override
    public Image getImage(Object element) {
        SWTUtils.checkThread();
        if (element instanceof EObject) {
            Image image = new StrategyEngineCoreSwitch<Image>() {
                @Override
                public Image caseStrategyEngine(StrategyEngine object) {
                    return getResourceManager()
                            .createImageWithDefault(
                                    StrategyEngineImage.ENGINE_OBJ
                                            .getImageDescriptor());
                }

                @Override
                public Image caseStrategy(Strategy object) {
                    return getResourceManager().createImageWithDefault(
                            StrategyEngineImage.STRATEGY_OBJ
                                    .getImageDescriptor());
                }
            }.doSwitch((EObject) element);
            if (image != null) {
                return image;
            }
        }
        return super.getImage(element);
    }

    /**
     * Provides the resource manager that will be disposed with this object.
     * Subclasses can use this to safely allocate images.
     * 
     * @return the resource manager.
     */
    protected LocalResourceManager getResourceManager() {
        return mResourceManager;
    }

    @Override
    public void dispose() {
        mPropertyWatcher.dispose();
        getResourceManager().dispose();
        super.dispose();
    }
}
