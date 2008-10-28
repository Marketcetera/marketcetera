package org.marketcetera.photon.module.preferences;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;

public abstract class SWTTestBase {

	protected static final Display display = PlatformUI.getWorkbench().getDisplay();
	protected Shell shell = null;
	
	@Before
	public void setUp() throws Exception {
		newShell();
	}

	private void newShell() {
		disposeShell();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.open();
	}

	private void disposeShell() {
		if (shell != null) {
			shell.dispose();
			shell = null;
		}
	}

	@After
	public void tearDown() throws Exception {
		display.syncExec(new Runnable() {		
			@Override
			public void run() {
				disposeShell();
			}
		});
	}
}