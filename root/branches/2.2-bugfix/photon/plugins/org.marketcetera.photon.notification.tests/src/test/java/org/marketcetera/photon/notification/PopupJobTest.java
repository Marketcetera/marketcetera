package org.marketcetera.photon.notification;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link PopupJob}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class PopupJobTest {
	
	@Test(expected=AssertionFailedException.class)
	public void testConstructor() {
		new PopupJob(null);
	}
}
