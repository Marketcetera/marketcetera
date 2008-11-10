package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

public class RubyProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	protected RubyProjectLibraryPage projectsPage;
	protected RubyProject workingProject;
	
	public RubyProjectPropertyPage() {
	}

	protected Control createContents(Composite parent)  {
		noDefaultAndApplyButton();

		workingProject = getRubyProject();
		if (workingProject == null || !workingProject.getProject().isOpen())
			return createClosedProjectPageContents(parent);

		return createProjectPageContents(parent);
	}
	
	protected RubyProject getRubyProject() {
		IAdaptable selectedElement = getElement();
		if (selectedElement == null)
			return null;

		if (selectedElement instanceof RubyProject)
			return (RubyProject) selectedElement;
			
		if (selectedElement instanceof IProject) {
			IProject simpleProject = (IProject) selectedElement;
			try {
				if (simpleProject.hasNature(RubyCore.NATURE_ID)) {
					RubyProject theRubyProject = new RubyProject();
					theRubyProject.setProject(simpleProject);
					return theRubyProject;
				}
			} catch(CoreException e) {
				RubyPlugin.log(e);
			}
		}
		
		return null;
	}
	
	protected Control createClosedProjectPageContents(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(RubyUIMessages.RubyProjectPropertyPage_rubyProjectClosed);
		
		return label;
	}
	
	protected Control createProjectPageContents(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayout(new GridLayout());	
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//tabChanged(e.item);
			}	
		});

		projectsPage = new RubyProjectLibraryPage(workingProject);		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(RubyUIMessages.RubyProjectLibraryPage_tabName);
//		tabItem.setData(projectsPage);
		tabItem.setControl(projectsPage.getControl(tabFolder));

		return tabFolder;
	}
	public boolean performOk() {
		try {
			projectsPage.getWorkingProject().save(null, true);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, RubyUIMessages.RubyProjectPropertyPage_performOkException, RubyUIMessages.RubyProjectPropertyPage_performOkExceptionDialogMessage);
		}
		return super.performOk();
	}

}
