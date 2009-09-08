package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DataBindingUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class DataBindingUtilsTest {

    @Test
    @UI
    public void testBindValue() {
        DataBindingContext dbc = new DataBindingContext();
        WritableValue value1 = WritableValue.withValueType(String.class);
        WritableValue value2 = new WritableValue("ABC", String.class);
        DataBindingUtils.bindValue(dbc, value1, value2);
        assertThat(value1.getValue(), is((Object) "ABC"));
        value1.setValue("");
        assertThat(value2.getValue(), nullValue());
    }

    @Test
    @UI
    public void testBindRequiredField() throws Exception {
        Shell s = new Shell();
        try {
            DataBindingContext dbc = new DataBindingContext();
            Text t = new Text(s, SWT.NONE);
            IObservableValue value1 = SWTObservables.observeText(t, SWT.Modify);
            WritableValue value2 = new WritableValue("ABC", String.class);
            DataBindingUtils.bindRequiredField(dbc, value1, value2, "t");
            assertThat(value1.getValue(), is((Object) "ABC"));
            value1.setValue("");
            assertThat(value2.getValue(), nullValue());
            SWTBotControlDecoration decoration = new SWTBotControlDecoration(
                    new SWTBotText(t));
            decoration.assertRequired(RequiredFieldSupportTest
                    .getRequiredValueMessage("t"));
        } finally {
            s.close();
        }
    }

    @Test
    @UI
    public void testObserveAndTrack() {
        // create a dynamic EMF object
        EPackage testPackage = EcoreFactory.eINSTANCE.createEPackage();
        EClass testClass = EcoreFactory.eINSTANCE.createEClass();
        testClass.setName("Test");
        testPackage.getEClassifiers().add(testClass);
        EAttribute testAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        testAttribute.setEType(EcorePackage.Literals.ESTRING);
        testAttribute.setName("test");
        testClass.getEStructuralFeatures().add(testAttribute);
        EObject testObject = testPackage.getEFactoryInstance().create(testClass);
        ObservablesManager mockManager = mock(ObservablesManager.class);
        IObservableValue observable = DataBindingUtils.observeAndTrack(
                mockManager, testObject,
                testAttribute); 
        verify(mockManager).addObservable(observable);
        observable.setValue("asdf");
        assertThat(testObject.eGet(testAttribute), is((Object) "asdf"));
    }

    @Test
    @UI
    public void testControlDecorationSupport() throws Exception {
        Shell s = new Shell();
        try {
            Text t = new Text(s, SWT.NONE);
            final IObservableValue value1 = SWTObservables.observeText(t, SWT.Modify);
            MultiValidator validator = new MultiValidator() {
                @Override
                protected IStatus validate() {
                    // call the getter to track it
                    value1.getValue();
                    return ValidationStatus.error("abc");
                }
            };
            DataBindingUtils.initControlDecorationSupportFor(validator);
            SWTBotControlDecoration decoration = new SWTBotControlDecoration(
                    new SWTBotText(t));
            decoration.assertError("abc");
        } finally {
            s.close();
        }
    }

}
