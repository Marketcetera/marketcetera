package org.marketcetera.photon.test;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.swt.finder.junit.SWTBotApplicationLauncherClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Captures screenshots on failure notifications.
 * 
 * @author Hans Schwaebli (Bug 259787)
 * @version $Id$
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
/* $License$ */

/**
 * Captures screenshots on failure notifications.
 * <p>
 * Copied from SWTBot internal class
 * <code>org.eclipse.swtbot.swt.finder.junit.ScreenshotCaptureListener</code>.
 * See <a
 * href="http://bugs.eclipse.org/283167">http://bugs.eclipse.org/283167</a>.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
final class ScreenshotCaptureListener extends RunListener {
    /** The logger. */
    private static Logger log = Logger
            .getLogger(SWTBotApplicationLauncherClassRunner.class);

    /** Counts the screenshots to determine if maximum number is reached. */
    private static int screenshotCounter = 0;

    public void testFailure(Failure failure) throws Exception {
        captureScreenshot(failure);
    }

    private void captureScreenshot(Failure failure) {
        try {
            int maximumScreenshots = SWTBotPreferences.MAX_ERROR_SCREENSHOT_COUNT;
            String fileName = SWTBotPreferences.SCREENSHOTS_DIR
                    + "/" + failure.getTestHeader() + "." + SWTBotPreferences.SCREENSHOT_FORMAT.toLowerCase(); //$NON-NLS-1$
            if (++screenshotCounter <= maximumScreenshots) {
                new File("screenshots").mkdirs(); //$NON-NLS-1$ 
                captureScreenshot(fileName);
            } else {
                log
                        .info("No screenshot captured for '" + failure.getTestHeader() + "' because maximum number of screenshots reached: " //$NON-NLS-1$ 
                                + maximumScreenshots);
            }
        } catch (Exception e) {
            log.warn("Could not capture screenshot", e); //$NON-NLS-1$
        }
    }

    private boolean captureScreenshot(String fileName) {
        return SWTUtils.captureScreenshot(fileName);
    }

    public int hashCode() {
        return 31;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

}