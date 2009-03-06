package org.marketcetera.photon.ui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

public class ContextMenuFactory {
	public void createContextMenu(String name, final Table table,
			IWorkbenchPartSite site) {
		createContextMenu(name, table, site, site.getSelectionProvider());
	}

	public void createContextMenu(String name, final Table table,
			IWorkbenchPartSite site, ISelectionProvider selectionProvider) {
		Menu menu;
		Menu existingMenu = table.getMenu();
		MenuManager menuMgr = new MenuManager(name);

		if (existingMenu != null) {
			menu = existingMenu;
		} else {
			menu = menuMgr.createContextMenu(table);
			menuMgr
					.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		}

		table.setMenu(menu);
		table.setData(MenuManager.class.toString(), menuMgr);
		site.registerContextMenu(menuMgr, selectionProvider);
	}
}
