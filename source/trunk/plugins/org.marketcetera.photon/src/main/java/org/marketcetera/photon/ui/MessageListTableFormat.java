package org.marketcetera.photon.ui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;



public class MessageListTableFormat extends EnumTableFormat {

	private final IWorkbenchPartSite site;
	private final ISelectionProvider selectionProvider;

	public MessageListTableFormat(Table table, Enum[] columns, IWorkbenchPartSite site) {
		super(table, columns);
		this.site = site;
		this.selectionProvider = site.getSelectionProvider();
		
        createContextMenu("messagesContextMenu",table);
        hookColumnChooserMenu(table);

	}

	private void hookColumnChooserMenu(final Table table) {
		// we're hooking up a submenu using straight-up swt (as opposed to contribution items)
		// since this is how it will eventually work in swt post-3.2 when table supports 
		// attaching a pop-up menu to the column headers.
		final Menu menu = table.getMenu();
		
		menu.addListener(SWT.Show, new Listener() {
	
			private MenuItem columnsCascadeItem;
	
			public void handleEvent(Event e) {
				if (menu.getItemCount() > 0 && menu.getItem(menu.getItemCount() - 1).equals(columnsCascadeItem))  // this menu already has our additions
					return;
				
				if (menu.getItemCount() != 0) {
					new MenuItem(menu, SWT.SEPARATOR);
				}
				
				columnsCascadeItem = new MenuItem(menu, SWT.CASCADE);
				columnsCascadeItem.setText("Choose columns");
				Menu columnsCascadeMenu = new Menu(table.getShell(), SWT.DROP_DOWN);
				columnsCascadeItem.setMenu(columnsCascadeMenu);
				
				for(final TableColumn column : table.getColumns()) {
					MenuItem item = new MenuItem(columnsCascadeMenu, SWT.CHECK);
					item.setText(column.getText());
					item.setEnabled(true);
					item.setSelection(!isColumnHidden(column));
					
					item.addListener(SWT.Selection, 
							new Listener() {
								public void handleEvent(Event event) {
									if (isColumnHidden(column))
										showColumn(column);
									else
										hideColumn(column);
								}
							});
				}
				
				new MenuItem(columnsCascadeMenu, SWT.SEPARATOR);
				
				MenuItem moreColumnsItem = new MenuItem(columnsCascadeMenu, SWT.PUSH);
				moreColumnsItem.setText("More columns...");
				moreColumnsItem.setEnabled(false);
			}
		});
	}

	private void createContextMenu(String name, final Table table) {
		Menu menu;
		Menu existingMenu = table.getMenu();
		MenuManager menuMgr = new MenuManager(name);
	
		if (existingMenu != null){
			menu = existingMenu;
		} else {
			menu = menuMgr.createContextMenu(table);
			menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	
		table.setMenu(menu);
		site.registerContextMenu(menuMgr, selectionProvider);
	}

}
