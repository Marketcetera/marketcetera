package org.marketcetera.photon.test;

import java.util.Locale;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;

/* $License$ */

/**
 * Extends {@link TestCaseBase} to ensure all logs categories are off and
 * process active local is Locale.ROOT.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PhotonTestBase extends TestCaseBase {

    @Override
    public void setupTestCaseBase() {
        BasicConfigurator.resetConfiguration();
        super.setupTestCaseBase();
        setDefaultLevel(Level.OFF);
        ActiveLocale.pushLocale(Locale.ROOT);
    }
}
