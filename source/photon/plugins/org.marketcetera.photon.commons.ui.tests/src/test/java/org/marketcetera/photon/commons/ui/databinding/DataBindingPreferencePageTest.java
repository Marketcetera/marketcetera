package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test {@link DataBindingPreferencePage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class DataBindingPreferencePageTest extends PhotonTestBase {

    private class TestPreferencePage extends DataBindingPreferencePage {

        private WritableValue value1;
        private WritableValue value2;
        private Binding binding;

        public TestPreferencePage() {
            value1 = WritableValue.withValueType(String.class);
            value2 = WritableValue.withValueType(String.class);
            binding = getDataBindingContext().bindValue(
                    value1,
                    value2,
                    new UpdateValueStrategy()
                            .setAfterGetValidator(new IValidator() {
                                @Override
                                public IStatus validate(Object value) {
                                    // program "asdf" to trigger an error
                                    if ("asdf".equals(value)) {
                                        return ValidationStatus.error("asdf");
                                    } else {
                                        return ValidationStatus.ok();
                                    }
                                }
                            }), null);
            getObservablesManager().addObservablesFromContext(
                    getDataBindingContext(), true, true);
        }

        @Override
        protected Control createContents(Composite parent) {
            return null;
        }

    }

    @Test
    @UI
    public void test() {
        TestPreferencePage fixture = new TestPreferencePage();
        assertThat(fixture.isValid(), is(true));
        fixture.value1.setValue("asdf");
        assertThat(fixture.isValid(), is(false));
        fixture.value1.setValue(null);
        assertThat(fixture.isValid(), is(true));
        fixture.dispose();
        assertThat(fixture.binding.isDisposed(), is(true));
        assertThat(fixture.value1.isDisposed(), is(true));
        assertThat(fixture.value2.isDisposed(), is(true));
    }
}
