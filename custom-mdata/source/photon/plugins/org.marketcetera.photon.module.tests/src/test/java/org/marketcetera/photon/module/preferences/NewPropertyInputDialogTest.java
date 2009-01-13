package org.marketcetera.photon.module.preferences;

import static org.junit.Assert.assertFalse;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/* $License$ */

/**
 * Tests {@link NewPropertyInputDialog}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class NewPropertyInputDialogTest {

	@Test
	public void testDialogOpen() {
		final NewPropertyInputDialog fixture = new NewPropertyInputDialog(null, false);
		fixture.setBlockOnOpen(false);
		fixture.open();
		final Shell shell = fixture.getShell();
		assertFalse(shell.isDisposed());
		shell.dispose();
	}

}
