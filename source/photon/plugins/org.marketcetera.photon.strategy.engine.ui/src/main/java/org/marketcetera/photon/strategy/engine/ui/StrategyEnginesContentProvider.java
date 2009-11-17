package org.marketcetera.photon.strategy.engine.ui;

import java.text.MessageFormat;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.IEMFListProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.photon.commons.ui.IdentityComparer;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ForwardingObject;

/* $License$ */

/**
 * Tree content provider for strategy engines and their deployed strategies. It
 * expects as input an {@link IObservableList} of {@link StrategyEngine}
 * objects. The input list will not be modified.
 * <p>
 * Instances of this class are thread confined. They can only be instantiated
 * and accessed on a single UI thread. Some additional caveats to note:
 * <ul>
 * <li>The input IObservableList must be on the realm of the current display.
 * None of its elements should be modified in any way, except in that realm,
 * i.e. the UI thread.</li>
 * <li>The viewer must have an IElementComparer that declares the input list
 * equal to itself despite changes to its contents. {@link IdentityComparer} can
 * be used for this purpose.</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEnginesContentProvider extends ForwardingObject implements
        ITreeContentProvider {

    private final ObservableListTreeContentProvider mDelegate;

    /**
     * Constructor. Must be called from the realm of the current display.
     */
    public StrategyEnginesContentProvider() {
        mDelegate = new ObservableListTreeContentProvider(new FactoryImpl(),
                new TreeStructureAdvisorImpl());
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return delegate().getChildren(parentElement);
    }

    @Override
    public Object getParent(Object element) {
        return delegate().getParent(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        return delegate().hasChildren(element);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return delegate().getElements(inputElement);
    }

    @Override
    public void dispose() {
        delegate().dispose();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // validate input up front
        if (newInput != null) {
            Assert
                    .isLegal(
                            newInput instanceof IObservableList,
                            MessageFormat
                                    .format(
                                            "input [{0}] is not an IObservableList", newInput.getClass())); //$NON-NLS-1$
            Realm realm = ((IObservableList) newInput).getRealm();
            Assert
                    .isLegal(
                            realm.isCurrent(),
                            MessageFormat
                                    .format(
                                            "input [{0} on realm {1}] must be on the current display realm", newInput.getClass(), realm)); //$NON-NLS-1$;
            Object elementType = ((IObservableList) newInput).getElementType();
            Assert
                    .isLegal(
                            elementType == StrategyEngine.class,
                            MessageFormat
                                    .format(
                                            "input [{0} with element type {1}] should have element type StrategyEngine.class", newInput, elementType)); //$NON-NLS-1$;
            Assert.isLegal(viewer != null, "viewer cannot be null"); //$NON-NLS-1$;
            Assert
                    .isLegal(
                            viewer instanceof AbstractTreeViewer,
                            MessageFormat
                                    .format(
                                            "viewer [{0}] is not an AbstractTreeViewer", viewer.getClass())); //$NON-NLS-1$;
            Assert.isLegal(((AbstractTreeViewer) viewer).getComparer() != null,
                    "viewer must have an IElementComparer set"); //$NON-NLS-1$;
        }
        delegate().inputChanged(viewer, oldInput, newInput);
    }

    @Override
    protected ObservableListTreeContentProvider delegate() {
        return mDelegate;
    }

    /**
     * Returns the set of elements known to this content provider. Label
     * providers may track this set if they need to be notified about additions
     * before the viewer sees the added element, and notified about removals
     * after the element was removed from the viewer. This is intended for use
     * by label providers, as it will always return the items that need labels.
     * 
     * @return readableSet of items that will need labels
     */
    public IObservableSet getKnownElements() {
        return delegate().getKnownElements();
    }

    /**
     * Returns the set of known elements which have been realized in the viewer.
     * Clients may track this set in order to perform custom actions on elements
     * while they are known to be present in the viewer.
     * 
     * @return the set of known elements which have been realized in the viewer.
     */
    public IObservableSet getRealizedElements() {
        return delegate().getRealizedElements();
    }

    /**
     * Factory that creates observable lists of children for a given node.
     */
    @ClassVersion("$Id$")
    private static class FactoryImpl implements IObservableFactory {

        private final static IEMFListProperty children = EMFProperties
                .list(StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__DEPLOYED_STRATEGIES);

        public IObservable createObservable(final Object target) {
            if (target instanceof IObservableList) {
                /*
                 * A new delegating list is created since the content provider
                 * will dispose it and we do not want to dispose the input list.
                 */
                return Observables
                        .unmodifiableObservableList((IObservableList) target);
            } else if (target instanceof StrategyEngine) {
                return children.observe(target);
            }
            return null;
        }
    }

    /**
     * Helps/optimizes tree rendering. Not overriding getChildren because we
     * want the child lists tracked.
     * 
     * @See {@link TreeStructureAdvisor}
     */
    @ClassVersion("$Id$")
    private static class TreeStructureAdvisorImpl extends TreeStructureAdvisor {

        @Override
        public Object getParent(Object element) {
            if (element instanceof DeployedStrategy) {
                return ((DeployedStrategy) element).getEngine();
            }
            return null;
        }
    }

}
