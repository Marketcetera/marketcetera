package org.marketcetera.photon.notification;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link PopupJob}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PopupJobTest {
	
	@Test(expected=AssertionFailedException.class)
	public void testConstructor() {
		new PopupJob(null);
	}
}
