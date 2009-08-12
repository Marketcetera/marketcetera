package org.marketcetera.photon.commons.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ui.ColorManagerTest;
import org.marketcetera.photon.commons.ui.DisplayThreadExecutorTest;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest;
import org.marketcetera.photon.commons.ui.LocalizedLabelTest;
import org.marketcetera.photon.commons.ui.SWTUtilsTest;
import org.marketcetera.photon.commons.ui.databinding.DataBindingContextCaveatTest;
import org.marketcetera.photon.commons.ui.databinding.MessagesTest;
import org.marketcetera.photon.commons.ui.databinding.ObservingCompositeTest;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcherTest;
import org.marketcetera.photon.commons.ui.databinding.ProxyObservablesTest;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupportTest;
import org.marketcetera.photon.commons.ui.databinding.UpdateStrategyFactoryTest;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenuTest;
import org.marketcetera.photon.commons.ui.table.TableSupportTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { TableSupportTest.class, ChooseColumnsMenuTest.class,
        ColorManagerTest.class, PropertyWatcherTest.class,
        ProxyObservablesTest.class, DataBindingContextCaveatTest.class,
        UpdateStrategyFactoryTest.class, RequiredFieldSupportTest.class,
        MessagesTest.class,
        org.marketcetera.photon.commons.ui.MessagesTest.class,
        SWTUtilsTest.class, JFaceUtilsTest.class, LocalizedLabelTest.class,
        DisplayThreadExecutorTest.class, ObservingCompositeTest.class })
public class CommonsUISuite {
}
