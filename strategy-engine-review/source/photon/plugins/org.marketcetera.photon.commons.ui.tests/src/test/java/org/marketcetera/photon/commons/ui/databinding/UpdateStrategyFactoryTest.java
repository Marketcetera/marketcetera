package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;


/* $License$ */

/**
 * Tests {@link UpdateStrategyFactory}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class UpdateStrategyFactoryTest extends PhotonTestBase {

    @Test
    @UI
    public void testEMFUpdateValueStrategyWithEmptyStringToNull() throws Exception {
        UpdateValueStrategy fixture = UpdateStrategyFactory.createEMFUpdateValueStrategyWithEmptyStringToNull();
        DataBindingContext dbc = new DataBindingContext();
        WritableValue value1 = WritableValue.withValueType(String.class);
        WritableValue value2 = new WritableValue("ABC", String.class);
        dbc.bindValue(value1, value2, fixture, null);
        assertThat(value1.getValue(), is((Object) "ABC"));
        value1.setValue("");
        assertThat(value2.getValue(), nullValue());
    }
}
