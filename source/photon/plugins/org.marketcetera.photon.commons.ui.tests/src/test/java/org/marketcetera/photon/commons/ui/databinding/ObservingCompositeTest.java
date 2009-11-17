package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link ObservingComposite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class ObservingCompositeTest extends PhotonTestBase {

    @Test
    @UI
    public void testAutomaticDisposal() throws Exception {
        Shell s = new Shell();
        final WritableValue observable = WritableValue.withValueType(null);
        class TestComposite extends ObservingComposite {
            public TestComposite(Composite parent) {
                super(parent);
                getObservablesManager().addObservable(observable);
            }
        }
        new TestComposite(s);
        s.open();
        s.close();
        s.dispose();
        assertThat(observable.isDisposed(), is(true));
    }
}
