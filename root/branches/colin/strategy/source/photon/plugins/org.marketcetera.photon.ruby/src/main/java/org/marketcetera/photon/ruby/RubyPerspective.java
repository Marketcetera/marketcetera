package org.marketcetera.photon.ruby;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.rubypeople.rdt.internal.ui.RdtPerspectiveFactory;
import org.rubypeople.rdt.ui.IRubyConstants;
import org.rubypeople.rdt.ui.RubyUI;

/* $License$ */

/**
 * This perspective integrates RDT into Photon.  It is used instead of {@link RdtPerspectiveFactory}
 * to provide more control.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.9.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class RubyPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);//$NON-NLS-1$
		folder.addView("org.marketcetera.photon.RubyExplorer"); //$NON-NLS-1$

		IFolderLayout consoleArea = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.65, editorArea);//$NON-NLS-1$
		consoleArea.addView(IPageLayout.ID_PROBLEM_VIEW);

		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);
		
		layout.addActionSet(RubyUI.ID_ACTION_SET);
		layout.addActionSet(RubyUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		// new actions - Ruby project creation wizard
		layout.addNewWizardShortcut(IRubyConstants.ID_NEW_CLASS_WIZARD); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
    }

}