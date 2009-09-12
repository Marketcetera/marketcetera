package org.marketcetera.photon.commons.ui.workbench.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ui.workbench.ChooseColumnsMenuTest;
import org.marketcetera.photon.commons.ui.workbench.DataBindingPropertyPageTest;
import org.marketcetera.photon.commons.ui.workbench.MessagesTest;
import org.marketcetera.photon.commons.ui.workbench.SafeHandlerTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { MessagesTest.class, DataBindingPropertyPageTest.class,
        ChooseColumnsMenuTest.class, SafeHandlerTest.class })
public final class CommonsWorkbenchSuite {
}
