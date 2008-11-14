package org.rubypeople.rdt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class DeprecatedView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		GridData data = new GridData();
		data.widthHint = 150;
		container.setLayoutData(data);
		
		Label label = new Label(container, SWT.NULL);
		label.setText("This view has been replaced by the new Ruby Explorer view.");
		
		Button button = new Button(container, SWT.NULL);
		button.setText("Open Ruby Explorer");
		final IViewPart view = this;
		button.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (dw == null) return;
					IWorkbenchPage page = dw.getActivePage();
					if (page == null) return;
					page.showView(RubyUI.ID_RUBY_EXPLORER);
					page.hideView(view);
				} catch (PartInitException e1) {
					RubyPlugin.log(e1);
				}
			}
		
		});
	}

	@Override
	public void setFocus() {
	}

}
