package org.marketcetera.photon.commons.emf;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ForwardingObject;

/* $License$ */

/**
 * A delegating EObject. Subclasses can override to compose an EObject in a
 * custom way.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class ForwardingEObject extends ForwardingObject implements EObject {

    private final EObject mDelegate;

    /**
     * Constructor.
     * 
     * @param delegate
     *            the delegate to forward to
     */
    public ForwardingEObject(EObject delegate) {
        mDelegate = delegate;
    }

    @Override
    protected EObject delegate() {
        return mDelegate;
    }

    @Override
    public TreeIterator<EObject> eAllContents() {
        return delegate().eAllContents();
    }

    @Override
    public EClass eClass() {
        return delegate().eClass();
    }

    @Override
    public EObject eContainer() {
        return delegate().eContainer();
    }

    @Override
    public EStructuralFeature eContainingFeature() {
        return delegate().eContainingFeature();
    }

    @Override
    public EReference eContainmentFeature() {
        return delegate().eContainmentFeature();
    }

    @Override
    public EList<EObject> eContents() {
        return delegate().eContents();
    }

    @Override
    public EList<EObject> eCrossReferences() {
        return delegate().eCrossReferences();
    }

    @Override
    public Object eGet(EStructuralFeature feature) {
        return delegate().eGet(feature);
    }

    @Override
    public Object eGet(EStructuralFeature feature, boolean resolve) {
        return delegate().eGet(feature, resolve);
    }

    @Override
    public boolean eIsProxy() {
        return delegate().eIsProxy();
    }

    @Override
    public boolean eIsSet(EStructuralFeature feature) {
        return delegate().eIsSet(feature);
    }

    @Override
    public Resource eResource() {
        return delegate().eResource();
    }

    @Override
    public void eSet(EStructuralFeature feature, Object newValue) {
        delegate().eSet(feature, newValue);
    }

    @Override
    public void eUnset(EStructuralFeature feature) {
        delegate().eUnset(feature);
    }

    @Override
    public EList<Adapter> eAdapters() {
        return delegate().eAdapters();
    }

    @Override
    public boolean eDeliver() {
        return delegate().eDeliver();
    }

    @Override
    public void eNotify(Notification notification) {
        delegate().eNotify(notification);
    }

    @Override
    public void eSetDeliver(boolean deliver) {
        delegate().eSetDeliver(deliver);
    }
}
