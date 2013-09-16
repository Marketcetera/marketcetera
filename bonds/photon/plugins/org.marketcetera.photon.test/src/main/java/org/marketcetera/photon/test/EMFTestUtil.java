package org.marketcetera.photon.test;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/* $License$ */

/**
 * Utilities for testing with EMF EObjects.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EMFTestUtil {

    public static EObject createDynamicEObject() {
        EPackage testPackage = EcoreFactory.eINSTANCE.createEPackage();
        EClass testClass = EcoreFactory.eINSTANCE.createEClass();
        testClass.setName("Test");
        testPackage.getEClassifiers().add(testClass);
        EAttribute testAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        testAttribute.setEType(EcorePackage.Literals.ESTRING);
        testAttribute.setName("test");
        testClass.getEStructuralFeatures().add(testAttribute);
        return testPackage.getEFactoryInstance().create(testClass);
    }

    public static void eSet(EObject object, String attribute, Object value) {
        object.eSet(object.eClass().getEStructuralFeature(attribute), value);
    }

    public static Object eGet(EObject object, String attribute) {
        return object.eGet(object.eClass().getEStructuralFeature(attribute));
    }

    private EMFTestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
