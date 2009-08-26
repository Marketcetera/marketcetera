package org.marketcetera.photon.commons.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ui.ColorManagerTest;
import org.marketcetera.photon.commons.ui.DisplayThreadExecutorTest;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest;
import org.marketcetera.photon.commons.ui.LocalizedLabelMessageInfoProviderTest;
import org.marketcetera.photon.commons.ui.LocalizedLabelTest;
import org.marketcetera.photon.commons.ui.SWTUtilsTest;
import org.marketcetera.photon.commons.ui.databinding.CustomWizardPageSupportTest;
import org.marketcetera.photon.commons.ui.databinding.DataBindingContextCaveatTest;
import org.marketcetera.photon.commons.ui.databinding.DataBindingPreferencePageTest;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtilsTest;
import org.marketcetera.photon.commons.ui.databinding.ObservablesManagerCaveatTest;
import org.marketcetera.photon.commons.ui.databinding.ObservingCompositeTest;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcherTest;
import org.marketcetera.photon.commons.ui.databinding.ProxyObservablesTest;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupportTest;
import org.marketcetera.photon.commons.ui.databinding.UpdateStrategyFactoryTest;
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
@Suite.SuiteClasses( { TableSupportTest.class,
        DataBindingPreferencePageTest.class,
        ObservablesManagerCaveatTest.class, ColorManagerTest.class,
        PropertyWatcherTest.class, ProxyObservablesTest.class,
        DataBindingContextCaveatTest.class, UpdateStrategyFactoryTest.class,
        RequiredFieldSupportTest.class,
        org.marketcetera.photon.commons.ui.databinding.MessagesTest.class,
        org.marketcetera.photon.commons.ui.MessagesTest.class,
        SWTUtilsTest.class, JFaceUtilsTest.class, LocalizedLabelTest.class,
        DisplayThreadExecutorTest.class, ObservingCompositeTest.class,
        LocalizedLabelMessageInfoProviderTest.class,
        CustomWizardPageSupportTest.class, DataBindingUtilsTest.class })
public class CommonsUISuite {
}
