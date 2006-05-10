package org.marketcetera.photon;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
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
import org.marketcetera.photon.actions.FocusCommandAction;
import org.marketcetera.photon.actions.OrderHistoryAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchWindow window;

	private IWorkbenchAction helpContentsAction;

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

	private IWorkbenchAction helpSearchAction;

	private IWorkbenchAction dynamicHelpAction;

	private IWorkbenchAction aboutAction;
	
	private IWorkbenchAction orderHistoryAction;

	private CommandStatusLineContribution commandStatusLineContribution;

	private FeedStatusLineContribution feedStatusLineContribution;

	private IWorkbenchAction focusCommandAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		this.window = window;

		commandStatusLineContribution = new CommandStatusLineContribution(CommandStatusLineContribution.ID);
		commandStatusLineContribution.addCommandListener(Application.getOrderManager().getCommandListener());
		feedStatusLineContribution = new FeedStatusLineContribution("feedStatus", new String[] {JMSConnector.JMS_CONNECTOR_ID});
		Application.getJMSConnector().addFeedComponentListener(feedStatusLineContribution);
		
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
		helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);  register(helpContentsAction);
		helpSearchAction = ActionFactory.HELP_SEARCH.create(window);  register(helpSearchAction);
		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);  register(dynamicHelpAction);
		aboutAction = ActionFactory.ABOUT.create(window);  register(aboutAction);

		//viewSecurityAction = new ViewSecurityAction(window);
		focusCommandAction = new FocusCommandAction(window, commandStatusLineContribution);  register(focusCommandAction);
		orderHistoryAction = new OrderHistoryAction(window);
	}

	@SuppressWarnings("deprecation")
	protected void fillMenuBar(IMenuManager menuBar) {
		// File menu
		MenuManager menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_FileMenuName);
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
		menuBar.add(menu);

		// Window menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_WindowMenuName);
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
		menu.add(new Separator());
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		menu.add(preferencesAction);
		menu.add(viewMenu);

		menuBar.add(menu);

		// Script menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_ScriptMenuName,
				IWorkbenchActionConstants.M_WINDOW);
		menu.add(new Separator());
		menuBar.add(menu);

		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		// Help menu
		menu = new MenuManager(
				Messages.ApplicationActionBarAdvisor_HelpMenuName,
				IWorkbenchActionConstants.M_WINDOW);
		register(helpContentsAction);
		menu.add(helpContentsAction);

		menu.add(helpSearchAction);
		menu.add(dynamicHelpAction);
		menu.add(new Separator());
		menu.add(aboutAction);
		menuBar.add(menu);

	}

	/*
	 * (non-Javadoc)
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
		ActionContributionItem orderHistoryCI = new ActionContributionItem(orderHistoryAction);
		toolBar.add(orderHistoryCI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(commandStatusLineContribution);
		statusLine.add(feedStatusLineContribution);

	}


}
