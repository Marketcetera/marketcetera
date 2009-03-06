package org.marketcetera.photon.module.ui;

import org.marketcetera.photon.internal.module.ui.SinkConsoleController;

public class ModuleUI {
	
	public static void installSinkConsole() {
		new SinkConsoleController().openConsole();
	}
}
