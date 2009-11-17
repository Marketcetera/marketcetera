package org.marketcetera.photon.commons.ui.workbench;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.workbench.DataBindingPropertyPage;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DataBindingPropertyPage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class DataBindingPropertyPageTest {

    @Test
    @UI
    public void test() throws Exception {
        DataBindingPropertyPage fixture = new DataBindingPropertyPage() {
            @Override
            protected Control createContents(Composite parent) {
                return null;
            }
        };
        IAdaptable element = mock(IAdaptable.class);
        fixture.setElement(element);
        assertThat(fixture.getElement(), sameInstance(element));
    }
}
