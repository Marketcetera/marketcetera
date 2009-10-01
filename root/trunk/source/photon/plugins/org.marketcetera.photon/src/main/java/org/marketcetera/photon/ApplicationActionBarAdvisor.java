package org.marketcetera.photon;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.marketcetera.photon.actions.CancelAllOpenOrdersAction;
import org.marketcetera.photon.actions.CheckForUpdatesAction;
import org.marketcetera.photon.actions.FocusCommandAction;
import org.marketcetera.photon.actions.WebHelpAction;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This class contains the initialization code for the main application
 * toolbars, action sets, menu bars, the cool bar, and the status bar.
 *
 * @author gmiller
 * @author andrei@lissovski.org
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ApplicationActionBarAdvisor
    extends ActionBarAdvisor
{

	private IWorkbenchAction saveAction;

	private IWorkbenchAction closeAllAction;

	private IWorkbenchAction closeAction;

	private IWorkbenchAction quitAction;

	private IWorkbenchAction undoAction;

	private IWorkbenchAction redoAction;

	private IWorkbenchAction cutAction;

	private IWorkbenchAction copyAction;

	private IWorkbenchAction create;

	private IWorkbenchAction deleteAction;

	private IWorkbenchAction selectAllAction;

	private IWorkbenchAction findAction;

	private IWorkbenchAction openNewWindowAction;

	private IWorkbenchAction newEditorAction;

	private IContributionItem perspectiveList;

	private IContributionItem viewList;

	private IWorkbenchAction savePerspectiveAction;

	private IWorkbenchAction resetPerspectiveAction;

	private IWorkbenchAction closePerspectiveAction;

	private IWorkbenchAction closeAllPerspectivesAction;

	private IWorkbenchAction maximizeAction;

	private IWorkbenchAction minimizeAction;

	private IWorkbenchAction activateEditorAction;

	private IWorkbenchAction nextEditorAction;

	private IWorkbenchAction previousEditorAction;

	private IWorkbenchAction showEditorAction;

	private IWorkbenchAction nextPerspectiveAction;

	private IWorkbenchAction previousPerspectiveAction;

	private IWorkbenchAction preferencesAction;

	//private IWorkbenchAction helpSearchAction;

	//private IWorkbenchAction dynamicHelpAction;

	private IWorkbenchAction aboutAction;

	private IWorkbenchAction focusCommandAction;

	private WebHelpAction webHelpAction;

    private IAction checkForUpdatesAction;

	private CancelAllOpenOrdersAction cancelAllOpenOrdersAction;

	private CommandContributionItemParameter reconnectServerParameter;
	
	private CommandContributionItemParameter reconnectMarketDataParameter;
	
	/**
	 * Eventually, actions will be set up declaratively using the org.eclipse.ui.menus
	 * extension point.  But for now, the menus are created in code, so menu contributions
	 * using the new API need to reference the command id from plugin.xml. This value must 
	 * be kept in synch.
	 */
	private static final String RECONNECT_SERVER_COMMAND_ID = "org.marketcetera.photon.reconnectServer"; //$NON-NLS-1$
	private static final String RECONNECT_MARKET_DATA_COMMAND_ID = "org.marketcetera.photon.reconnectMarketData"; //$NON-NLS-1$


	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * Constructs and registers all of the actions for the main application.
	 * Both prototype actions from ActionFactory and actions from the
	 * org.marketcetera.photon.actions package are used.
	 *
	 * @see ActionFactory
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	protected void makeActions(IWorkbenchWindow window) {
		saveAction = ActionFactory.SAVE.create(window);  register(saveAction);
		closeAllAction = ActionFactory.CLOSE_ALL.create(window);  register(closeAllAction);
		closeAction = ActionFactory.CLOSE.create(window);  register(closeAction);
		quitAction = ActionFactory.QUIT.create(window);  register(quitAction);
		undoAction = ActionFactory.UNDO.create(window);  register(undoAction);
		redoAction = ActionFactory.REDO.create(window);  register(redoAction);
		cutAction = ActionFactory.CUT.create(window);  register(cutAction);
		copyAction = ActionFactory.COPY.create(window);  register(copyAction);
		create = ActionFactory.PASTE.create(window);  register(create);
		deleteAction = ActionFactory.DELETE.create(window);  register(deleteAction);
		selectAllAction = ActionFactory.SELECT_ALL.create(window);  register(selectAllAction);
		findAction = ActionFactory.FIND.create(window);  register(findAction);
		openNewWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);  register(openNewWindowAction);
		newEditorAction = ActionFactory.NEW_EDITOR.create(window);  register(newEditorAction);
		perspectiveList = ContributionItemFactory.PERSPECTIVES_SHORTLIST
				.create(window);
		viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window);  register(savePerspectiveAction);
		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);  register(resetPerspectiveAction);
		closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create(window);  register(closePerspectiveAction);
		closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES
				.create(window);  register(closeAllPerspectivesAction);
		maximizeAction = ActionFactory.MAXIMIZE.create(window);  register(maximizeAction);
		minimizeAction = ActionFactory.MINIMIZE.create(window);  register(minimizeAction);
		activateEditorAction = ActionFactory.ACTIVATE_EDITOR.create(window);  register(activateEditorAction);
		nextEditorAction = ActionFactory.NEXT_EDITOR.create(window);  register(nextEditorAction);
		previousEditorAction = ActionFactory.PREVIOUS_EDITOR.create(window);  register(previousEditorAction);
		showEditorAction = ActionFactory.SHOW_EDITOR.create(window);  register(showEditorAction);
		nextPerspectiveAction = ActionFactory.NEXT_PERSPECTIVE.create(window);  register(nextPerspectiveAction);
		previousPerspectiveAction = ActionFactory.PREVIOUS_PERSPECTIVE
				.create(window);  register(previousPerspectiveAction);
		webHelpAction = new WebHelpAction(window);  register(webHelpAction);
//		helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);  register(helpContentsAction);
//		helpSearchAction = ActionFactory.HELP_SEARCH.create(window);  register(helpSearchAction);
//		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);  register(dynamicHelpAction);
        checkForUpdatesAction = new CheckForUpdatesAction(window);  register(checkForUpdatesAction);
		aboutAction = ActionFactory.ABOUT.create(window); register(aboutAction);
		reconnectServerParameter = new CommandContributionItemParameter(window, null, RECONNECT_SERVER_COMMAND_ID, SWT.PUSH);
		reconnectServerParameter.icon = PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_SERVER);
		reconnectServerParameter.mnemonic = Messages.APPLICATION_ACTION_BAR_ADVISOR_RECONNECT_SERVER_MNEMONIC.getText();
		reconnectMarketDataParameter = new CommandContributionItemParameter(window, null, RECONNECT_MARKET_DATA_COMMAND_ID, SWT.PUSH);
		reconnectMarketDataParameter.icon = PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_QUOTE_FEED);
		reconnectMarketDataParameter.mnemonic = Messages.APPLICATION_ACTION_BAR_ADVISOR_RECONNECT_MARKETDATA_MNEMONIC.getText();
		cancelAllOpenOrdersAction = new CancelAllOpenOrdersAction(); register(cancelAllOpenOrdersAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window); register(preferencesAction);
		focusCommandAction = new FocusCommandAction(window);  register(focusCommandAction);
	}

	/**
	 * Sets up the structure of the menu bars and menus, using actions defined in
	 * {@link #makeActions(IWorkbenchWindow)}
	 *
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	@SuppressWarnings("deprecation") //$NON-NLS-1$
	protected void fillMenuBar(IMenuManager menuBar) {
		// File menu
		MenuManager menu = new MenuManager(Messages.ApplicationActionBarAdvisor_FileMenuName.getText(),
				IWorkbenchActionConstants.M_FILE);
		menu.add(new CommandContributionItem(reconnectServerParameter));
		menu.add(new CommandContributionItem(reconnectMarketDataParameter));
		menu.add(cancelAllOpenOrdersAction);
		menu.add(new Separator());
		menu.add(saveAction);
		menu.add(new Separator());
		menu.add(closeAllAction);
		menu.add(closeAction);
		menu.add(new Separator());
		menu.add(quitAction);

		menuBar.add(menu);

		// Edit menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_EditMenuName.getText(),
				// Don't use IWorkbenchActionConstants.M_EDIT to avoid unwanted Ruby contributions
				"org.marketcetera.photon.editMenu");//$NON-NLS-1$
		menu.add(undoAction);
		menu.add(redoAction);
		menu.add(new Separator());
		menu.add(cutAction);
		menu.add(copyAction);
		menu.add(create);
		menu.add(new Separator());
		menu.add(deleteAction);
		menu.add(selectAllAction);
		menu.add(findAction);
		menu.add(new Separator());
		menu.add(preferencesAction);
		menuBar.add(menu);

		// Window menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_WindowMenuName.getText(),
				IWorkbenchActionConstants.M_WINDOW);
		menu.add(new Separator());
		MenuManager perspectiveMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_OpenPerspectiveMenuName.getText(),
				Messages.ApplicationActionBarAdvisor_OpenPerspectiveMenuID.getText());
		perspectiveList.update();
		perspectiveMenu.add(perspectiveList);
		menu.add(perspectiveMenu);
		MenuManager viewMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_OpenViewMenuItemName.getText(),
				IWorkbenchActionConstants.M_VIEW);
		viewMenu.add(viewList);
		menu.add(viewMenu);
		menu.add(new Separator());
		menu.add(savePerspectiveAction);
		menu.add(resetPerspectiveAction);
		menu.add(closePerspectiveAction);
		menu.add(closeAllPerspectivesAction);
		menu.add(new Separator());
		MenuManager subMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_NavigationMenuName.getText(),
				IWorkbenchActionConstants.M_NAVIGATE);
		subMenu.add(maximizeAction);
		subMenu.add(minimizeAction);
		subMenu.add(new Separator());
		subMenu.add(activateEditorAction);
		subMenu.add(nextEditorAction);
		subMenu.add(previousEditorAction);
		subMenu.add(showEditorAction);
		subMenu.add(new Separator());
		subMenu.add(nextPerspectiveAction);
		subMenu.add(previousPerspectiveAction);
		menu.add(subMenu);
		menu.add(viewMenu);
		menu.add(new Separator(IWorkbenchActionConstants.WINDOW_EXT));
		
		menuBar.add(menu);

		// Help menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_HelpMenuName.getText(),
				IWorkbenchActionConstants.M_HELP);

		menu.add(webHelpAction);
//		menu.add(helpContentsAction);
//		menu.add(helpSearchAction);
//		menu.add(dynamicHelpAction);
		menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator());
        menu.add(checkForUpdatesAction);
        menu.add(new Separator());
		menu.add(aboutAction);
		menuBar.add(menu);
	}

	/**
	 * Sets up the structure of the main application coolbar.
	 *
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
	 */
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolBar = new ToolBarManager(SWT.FLAT | SWT.TRAIL);
		coolBar.add(new ToolBarContributionItem(toolBar,"standard")); //$NON-NLS-1$

		ActionContributionItem focusCommandCI = new ActionContributionItem(focusCommandAction);
		toolBar.add(focusCommandCI);
		toolBar.add(new CommandContributionItem(reconnectServerParameter));
		toolBar.add(new CommandContributionItem(reconnectMarketDataParameter));
		ActionContributionItem cancelAllOpenOrdersCI = new ActionContributionItem(cancelAllOpenOrdersAction);
		toolBar.add(cancelAllOpenOrdersCI);
	}
	
}
