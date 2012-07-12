package org.marketcetera.photon.positions.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.marketcetera.photon.internal.positions.ui.MessagesTest;
import org.marketcetera.photon.internal.positions.ui.PositionsViewTablePageTest;
import org.marketcetera.photon.internal.positions.ui.PositionsViewTreePageTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(Suite.class)
@SuiteClasses( { MessagesTest.class, PositionsViewTablePageTest.class, PositionsViewTreePageTest.class })
public final class PositionsUISuite {
}
