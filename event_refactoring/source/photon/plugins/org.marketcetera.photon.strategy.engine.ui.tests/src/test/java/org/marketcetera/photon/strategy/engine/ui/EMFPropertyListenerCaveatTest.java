package org.marketcetera.photon.strategy.engine.ui;

import static org.junit.Assert.assertFalse;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

/* $License$ */

/**
 * Verifies an Eclipse bug <a
 * href="http://bugs.eclipse.org/291641">http://bugs.eclipse.org/291641</a>. If
 * this test fails then the workarounds in
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EMFPropertyListenerCaveatTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveMany() throws Exception {
        /*
         * Set up a dynamic EMF object
         */
        EPackage testPackage = EcoreFactory.eINSTANCE.createEPackage();
        EClass testClass = EcoreFactory.eINSTANCE.createEClass();
        testClass.setName("Test");
        testPackage.getEClassifiers().add(testClass);
        final EReference testReference = EcoreFactory.eINSTANCE
                .createEReference();
        testReference.setEType(EcorePackage.Literals.ESTRING);
        testReference.setUpperBound(-1);
        testReference.setName("test");
        testClass.getEStructuralFeatures().add(testReference);
        final EObject testInstance = testPackage.getEFactoryInstance().create(
                testClass);
        final EList<String> list = (EList<String>) testInstance
                .eGet(testReference);
        list.add("one");
        list.add("two");

        /*
         * Bind to a WritableList and see the ArrayIndexOutOfBoundsException
         * printed to System.out.
         */
        Realm.runWithDefault(new Realm() {
            @Override
            public boolean isCurrent() {
                return true;
            }
        }, new Runnable() {
            public void run() {
                DataBindingContext dbc = new DataBindingContext();
                WritableList target = WritableList
                        .withElementType(String.class);
                Binding b = dbc.bindList(target, EMFProperties.list(
                        testReference).observe(testInstance));
                /*
                 * This will trigger an ArrayIndexOutOfBoundsException until the
                 * bug is fixed.
                 */
                list.clear();
                assertFalse(((IStatus) b.getValidationStatus().getValue()).isOK());
            }
        });
    }
}
