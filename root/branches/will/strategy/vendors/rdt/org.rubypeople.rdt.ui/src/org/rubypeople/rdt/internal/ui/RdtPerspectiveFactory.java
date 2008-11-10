package org.rubypeople.rdt.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.rubypeople.rdt.ui.IRubyConstants;
import org.rubypeople.rdt.ui.RubyUI;

public class RdtPerspectiveFactory implements IPerspectiveFactory {

	public RdtPerspectiveFactory() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);//$NON-NLS-1$
		folder.addView(RubyUI.ID_RUBY_EXPLORER);
		folder.addView(RubyUI.ID_TYPE_HIERARCHY);
		folder.addPlaceholder(IPageLayout.ID_RES_NAV);

		IFolderLayout consoleArea = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.65, editorArea);//$NON-NLS-1$
		consoleArea.addView(IPageLayout.ID_PROBLEM_VIEW);
		consoleArea.addView(IPageLayout.ID_TASK_LIST);
		consoleArea.addPlaceholder(IRubyConstants.RI_VIEW_ID);
		consoleArea.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		consoleArea.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleArea.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		consoleArea.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(RubyUI.ID_ACTION_SET);
		layout.addActionSet(RubyUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		// views - ruby
		layout.addShowViewShortcut(RubyUI.ID_RUBY_EXPLORER);
		layout.addShowViewShortcut(RubyUI.ID_TYPE_HIERARCHY);
		
		// views - search
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
		
		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		
		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
		
		// new actions - Ruby project creation wizard
		layout.addNewWizardShortcut(IRubyConstants.ID_NEW_CLASS_WIZARD); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
    }

}