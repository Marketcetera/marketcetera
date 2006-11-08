package org.marketcetera.photon;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
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
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.actions.CheckForUpdatesAction;
import org.marketcetera.photon.actions.FocusCommandAction;
import org.marketcetera.photon.actions.ReconnectJMSAction;
import org.marketcetera.photon.actions.WebHelpAction;
import org.marketcetera.quotefeed.IQuoteFeed;

/**
 * This class contains the initialization code for the main application
 * toolbars, action sets, menu bars, the cool bar, and the status bar.
 *
 * @author gmiller
 * @author andrei@lissovski.org
 */
@ClassVersion("$Id$")
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchWindow window;

	//private IWorkbenchAction helpContentsAction;

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

	private IWorkbenchAction reconnectJMSAction;

	private CommandStatusLineContribution commandStatusLineContribution;

	private FeedStatusLineContribution jmsStatusLineContribution;

	private FeedStatusLineContribution quoteFeedStatusLineContribution;

	private IWorkbenchAction focusCommandAction;

	private WebHelpAction webHelpAction;

    private IAction checkForUpdatesAction;



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
		this.window = window;

		commandStatusLineContribution = new CommandStatusLineContribution(CommandStatusLineContribution.ID);
		jmsStatusLineContribution = new FeedStatusLineContribution("jmsStatus", new String[] {JMSConnector.JMS_CONNECTOR_ID});
		Application.getJMSConnector().addFeedComponentListener(jmsStatusLineContribution);
		IQuoteFeed quoteFeed = Application.getQuoteFeed();
		String quoteFeedID = "Quote Feed";
		if (quoteFeed != null) quoteFeedID = quoteFeed.getID();
		quoteFeedStatusLineContribution = new FeedStatusLineContribution("quoteFeedStatus", new String[] {quoteFeedID });
		if (quoteFeed != null) quoteFeed.addFeedComponentListener(quoteFeedStatusLineContribution);

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
		aboutAction = ActionFactory.ABOUT.create(window);  register(aboutAction);
		reconnectJMSAction = new ReconnectJMSAction(); register(reconnectJMSAction);

		//viewSecurityAction = new ViewSecurityAction(window);
		focusCommandAction = new FocusCommandAction(window, commandStatusLineContribution);  register(focusCommandAction);
	}

	/**
	 * Sets up the structure of the menu bars and menus, using actions defined in
	 * {@link #makeActions(IWorkbenchWindow)}
	 *
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	@SuppressWarnings("deprecation")
	protected void fillMenuBar(IMenuManager menuBar) {
		// File menu
		MenuManager menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_FileMenuName);
		menu.add(reconnectJMSAction);
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
				Messages.ApplicationActionBarAdvisor_EditMenuName);
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
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		menu.add(preferencesAction);
		menuBar.add(menu);

		// Script menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_ScriptMenuName,
				Messages.ApplicationActionBarAdvisor_ScriptMenuID);
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));  //agl necessary since the RunScript action is contributed as an editorContribution (see plugin.xml) 
		menuBar.add(menu);

		// Contributions to the top-level menu
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		// Window menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_WindowMenuName,
				IWorkbenchActionConstants.M_WINDOW);
		//menu.add(viewSecurityAction);
		menu.add(new Separator());
		menu.add(openNewWindowAction);
		menu.add(newEditorAction);
		menu.add(new Separator());
		MenuManager perspectiveMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_OpenPerspectiveMenuName,
				Messages.ApplicationActionBarAdvisor_OpenPerspectiveMenuID);
		perspectiveList.update();
		perspectiveMenu.add(perspectiveList);
		menu.add(perspectiveMenu);
		MenuManager viewMenu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_OpenViewMenuItemName,
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
				Messages.ApplicationActionBarAdvisor_NavigationMenuName,
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
				Messages.ApplicationActionBarAdvisor_HelpMenuName);

		menu.add(webHelpAction);
//		menu.add(helpContentsAction);
//		menu.add(helpSearchAction);
//		menu.add(dynamicHelpAction);
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
		coolBar.add(new ToolBarContributionItem(toolBar, "standard"));

		//ActionContributionItem viewSecurityCI = new ActionContributionItem(viewSecurityAction);
		//toolBar.add(viewSecurityCI);
		ActionContributionItem focusCommandCI = new ActionContributionItem(focusCommandAction);
		toolBar.add(focusCommandCI);
		ActionContributionItem reconnectJMSCI = new ActionContributionItem(reconnectJMSAction);
		toolBar.add(reconnectJMSCI);
	}

	/**
	 * Creates the structure of the main application status line.
	 * Currently consisting of the command entry area, and the JMS
	 * feed status indicator.
	 *
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(commandStatusLineContribution);
		statusLine.add(jmsStatusLineContribution);
		statusLine.add(quoteFeedStatusLineContribution);

	}


}
