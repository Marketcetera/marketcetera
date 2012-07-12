package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test that should fail when <a
 * href="http://bugs.eclipse.org/287247">http://bugs.eclipse.org/287247</a> is
 * fixed, allowing {@link DataBindingPreferencePage} to be updated accordingly.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class ObservablesManagerCaveatTest extends PhotonTestBase {

    @Test
    @UI
    public void testDispose() {
        DataBindingContext context = new DataBindingContext();
        ObservablesManager manager = new ObservablesManager();
        manager.addObservablesFromContext(context, true, true);

        WritableValue value1 = WritableValue.withValueType(String.class);
        WritableValue value2 = WritableValue.withValueType(String.class);
        Binding binding = context.bindValue(value1, value2);

        context.dispose();
        manager.dispose();

        assertThat(binding.isDisposed(), is(true));
        // these should both be true when the bug is fixed
        assertThat(value1.isDisposed(), is(false));
        assertThat(value2.isDisposed(), is(false));
    }
}
