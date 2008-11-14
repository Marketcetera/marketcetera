/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.search;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class FiltersDialog extends SelectionStatusDialog {

	private CheckboxTableViewer fListViewer;
	private RubySearchResultPage fPage;
	private Button fLimitElementsCheckbox;
	private Text fLimitElementsField;
	
	private int fLimitElementCount= 1000;
	private boolean fLimitElements= false;

	public FiltersDialog(RubySearchResultPage page) {
		super(page.getSite().getShell());
		setTitle(org.rubypeople.rdt.internal.ui.search.SearchMessages.FiltersDialog_title); 
		setStatusLineAboveButtons(true);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fPage = page;
	}

	public MatchFilter[] getEnabledFilters() {
		Object[] result = getResult();
		MatchFilter[] filters = new MatchFilter[result.length];
		System.arraycopy(result, 0, filters, 0, filters.length);
		return filters;
	}

	public boolean isLimitEnabled() {
		return fLimitElements;
	}

	/**
	 * @return returns the number of entries to limit the filters entry to
	 */
	public int getElementLimit() {
		return fLimitElementCount;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite composite) {
		Composite parent = (Composite) super.createDialogArea(composite);
		initializeDialogUnits(composite);

		createTableLimit(parent);
		// Create list viewer
		Label l= new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText(org.rubypeople.rdt.internal.ui.search.SearchMessages.FiltersDialog_filters_label); 
		
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER);
		table.setFont(parent.getFont());
		fListViewer = new CheckboxTableViewer(table);
		

		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight= convertHeightInCharsToPixels(8);
		table.setLayoutData(data);

		fListViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				// Return the features's label.
				return ((MatchFilter) element).getName();
			}
		});

		// Set the content provider
		ArrayContentProvider cp = new ArrayContentProvider();
		fListViewer.setContentProvider(cp);
		fListViewer.setInput(MatchFilter.allFilters());
		fListViewer.setCheckedElements(fPage.getMatchFilters());

		l= new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText(org.rubypeople.rdt.internal.ui.search.SearchMessages.FiltersDialog_description_label); 
		final Text description = new Text(parent, SWT.LEFT | SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		description.setFont(parent.getFont());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = convertHeightInCharsToPixels(3);
		description.setLayoutData(data);
		fListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selectedElement != null)
					description.setText(((MatchFilter) selectedElement).getDescription());
				else
					description.setText(""); //$NON-NLS-1$
			}
		});
		return parent;
	}


	private void createTableLimit(Composite ancestor) {
		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		parent.setLayout(gl);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		parent.setLayoutData(gd);

		fLimitElementsCheckbox = new Button(parent, SWT.CHECK);
		fLimitElementsCheckbox.setText(org.rubypeople.rdt.internal.ui.search.SearchMessages.FiltersDialog_limit_label);  
		fLimitElementsCheckbox.setLayoutData(new GridData());

		fLimitElementsField = new Text(parent, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(6);
		fLimitElementsField.setLayoutData(gd);

		applyDialogFont(parent);

		fLimitElementsCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLimitValueEnablement();
			}

		});

		fLimitElementsField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				validateText();
			}
		});
		initLimit();
	}

	private void initLimit() {
		boolean limit = fPage.limitElements();
		int count = fPage.getElementLimit();
		fLimitElementsCheckbox.setSelection(limit);
		fLimitElementsField.setText(String.valueOf(count));

		updateLimitValueEnablement();
	}

	private void updateLimitValueEnablement() {
		fLimitElementsField.setEnabled(fLimitElementsCheckbox.getSelection());
	}

	protected void validateText() {
		String text = fLimitElementsField.getText();
		int value = -1;
		try {
			value = Integer.valueOf(text).intValue();
		} catch (NumberFormatException e) {

		}
		if (fLimitElementsCheckbox.getSelection() && value <= 0)
			updateStatus(new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, org.rubypeople.rdt.internal.ui.search.SearchMessages.FiltersDialog_limit_error, null)); 
		else
			updateStatus(new Status(IStatus.OK, RubyPlugin.getPluginId(), 0, "", null)); //$NON-NLS-1$
	}

	protected void computeResult() {
		fLimitElementCount= Integer.valueOf(fLimitElementsField.getText()).intValue();
		fLimitElements= fLimitElementsCheckbox.getSelection();

		setResult(Arrays.asList(fListViewer.getCheckedElements()));
	}
}
