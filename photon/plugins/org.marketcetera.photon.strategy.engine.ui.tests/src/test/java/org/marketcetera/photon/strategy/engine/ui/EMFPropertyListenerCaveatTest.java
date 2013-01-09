package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;

/* $License$ */

/**
 * Verifies an Eclipse bug <a
 * href="http://bugs.eclipse.org/291641">http://bugs.eclipse.org/291641</a>. If
 * this test fails then the workarounds in
 * <code>org.marketcetera.photon.internal.strategy.engine.sa.InternalStrategyAgentEngine#disconnect()</code>
 * and {@link AbstractStrategyEngineConnection#refresh()} can be removed.
 * Ignored tests in {@link StrategyEnginesContentProviderTest} can also be run.
 * <p>
 * See also note on {@link StrategyEngine#getDeployedStrategies()}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: EMFPropertyListenerCaveatTest.java 10885 2009-11-17 19:22:56Z
 *          klim $
 * @since 2.0.0
 */
public class EMFPropertyListenerCaveatTest {

    private abstract static class TestTemplate {
        public TestTemplate() {
            /*
             * Set up a dynamic EMF object.
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
            final EObject testInstance = testPackage.getEFactoryInstance()
                    .create(testClass);
            @SuppressWarnings("unchecked")
            final EList<String> list = (EList<String>) testInstance
                    .eGet(testReference);
            list.add("one");
            list.add("two");
            list.add("three");

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
                    dbc.bindList(target, EMFProperties.list(testReference)
                            .observe(testInstance));
                    doTest(list, target);
                }
            });
        }

        protected abstract void doTest(final EList<String> list,
                WritableList target);
    }

    @Test
    public void testRemoveAll() throws Exception {
        new TestTemplate() {
            @Override
            protected void doTest(EList<String> list, WritableList target) {
                list.removeAll(Arrays.asList("one", "two", "three"));
                /*
                 * This is the bug: size should be 0 after all are removed...
                 */
                assertThat(target.size(), is(1));
            }
        };
    }

    @Test
    public void testClear() throws Exception {
        new TestTemplate() {
            @Override
            protected void doTest(EList<String> list, WritableList target) {
                list.clear();
                /*
                 * This is the bug: size should be 0 after clear...
                 */
                assertThat(target.size(), is(1));
            }
        };
    }
}
