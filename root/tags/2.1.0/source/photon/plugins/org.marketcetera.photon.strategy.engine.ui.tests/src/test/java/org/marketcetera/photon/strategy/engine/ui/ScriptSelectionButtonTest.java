package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.PhotonTestBase;


/* $License$ */

/**
 * Tests {@link ScriptSelectionButton};
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class ScriptSelectionButtonTest extends PhotonTestBase {

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("text") {
            @Override
            protected void run() throws Exception {
                new ScriptSelectionButton(null) {
                    @Override
                    public String selectScript(Shell shell, String current) {
                        return null;
                    }
                };
            }
        };
    }
}
