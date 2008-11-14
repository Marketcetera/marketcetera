package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.ElementListSelectionDialog;

public class RubyProjectSelector extends ResourceSelector {

	public RubyProjectSelector(Composite parent) {
		super(parent);
		
		browseDialogTitle = "Project Selection";
	}

	public IProject getSelection() {
		String projectName = getSelectionText();
		if (projectName != null && !projectName.equals(""))
			return RubyPlugin.getWorkspace().getRoot().getProject(projectName);
			
		return null;
	}

	protected void handleBrowseSelected() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(browseDialogTitle);
		dialog.setMessage(browseDialogMessage);
		dialog.setElements(RubyCore.getRubyProjects());

		if (dialog.open() == Dialog.OK) {
			textField.setText(((IProject) dialog.getFirstResult()).getName());
		}
	}

	protected String validateResourceSelection() {
		IProject project = getSelection();
		return project == null ? EMPTY_STRING : project.getName();
	}
}