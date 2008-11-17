package org.rubypeople.rdt.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.IUpdate;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class RubyActionGroup extends ActionGroup {
	
	/**
	 * Pop-up menu: id of the source sub menu (value <code>org.rubypeople.rdt.ui.source.menu</code>).
	 * 
	 * @since 1.0
	 */
	public static final String MENU_ID= "org.rubypeople.rdt.ui.source.menu"; //$NON-NLS-1$
	
	/**
	 * Pop-up menu: id of the generate group of the source sub menu (value
	 * <code>generateGroup</code>).
	 * 
	 * @since 1.0
	 */
	public static final String GROUP_GENERATE= "generateGroup";  //$NON-NLS-1$

	/**
	 * Pop-up menu: id of the code group of the source sub menu (value
	 * <code>codeGroup</code>).
	 * 
	 * @since 1.0
	 */
	public static final String GROUP_CODE= "codeGroup";  //$NON-NLS-1$

	/**
	 * Pop-up menu: id of the comment group of the source sub menu (value
	 * <code>commentGroup</code>).
	 * 
	 * TODO: Make API
	 */
	private static final String GROUP_COMMENT= "commentGroup"; //$NON-NLS-1$

	/**
	 * Pop-up menu: id of the edit group of the source sub menu (value
	 * <code>editGroup</code>).
	 * 
	 * TODO: Make API
	 */
	public static final String GROUP_EDIT= "editGroup"; //$NON-NLS-1$

	private RubyEditor fEditor;
	private String fGroupName;

	public RubyActionGroup(RubyEditor editor, String groupName) {
		fEditor= editor;
		fGroupName= groupName;
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		String menuText= "Source";//ActionMessages.SourceMenu_label; 
//		if (fQuickAccessAction != null) {
//			menuText= fQuickAccessAction.addShortcut(menuText); 
//		}
		IMenuManager subMenu= new MenuManager(menuText, MENU_ID); 
		int added= 0;
		if (isEditorOwner()) {
			added= fillEditorSubMenu(subMenu);
		} else {
			added= fillViewSubMenu(subMenu);
		}
		if (added > 0)
			menu.appendToGroup(fGroupName, subMenu);
	}
	
	private int fillEditorSubMenu(IMenuManager source) {
		int added= 0;
		source.add(new Separator(GROUP_COMMENT));
		added+= addEditorAction(source, "ToggleComment"); //$NON-NLS-1$
		added+= addEditorAction(source, "AddBlockComment"); //$NON-NLS-1$
		added+= addEditorAction(source, "RemoveBlockComment"); //$NON-NLS-1$
		source.add(new Separator(GROUP_EDIT));
		added+= addEditorAction(source, "Indent"); //$NON-NLS-1$
		added+= addEditorAction(source, "Format"); //$NON-NLS-1$
		added+= addEditorAction(source, "SurroundWithBeginRescue"); //$NON-NLS-1$
		source.add(new Separator(GROUP_GENERATE));
		source.add(new Separator(GROUP_CODE));
		return added;
	}

	private int fillViewSubMenu(IMenuManager source) {
		int added= 0;
		source.add(new Separator(GROUP_COMMENT));
		source.add(new Separator(GROUP_EDIT));
		source.add(new Separator(GROUP_GENERATE));
		source.add(new Separator(GROUP_CODE));
		return added;
	}
	
	private int addAction(IMenuManager menu, IAction action) {
		if (action != null && action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}	
	
	private int addEditorAction(IMenuManager menu, String actionID) {
		if (fEditor == null)
			return 0;
		IAction action= fEditor.getAction(actionID);
		if (action == null)
			return 0;
		if (action instanceof IUpdate)
			((IUpdate)action).update();
		if (action.isEnabled()) {
			menu.add(action);
			return 1;
		}
		return 0;
	}
	
	private boolean isEditorOwner() {
		return fEditor != null;
	}	
}
