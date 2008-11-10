/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.folding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingPreferenceBlock;


/**
 * Empty preference block for extensions to the
 * <code>org.eclipse.jdt.ui.javaFoldingStructureProvider</code> extension
 * point that do not specify their own.
 * 
 * @since 3.0
 */
class EmptyRubyFoldingPreferenceBlock implements IRubyFoldingPreferenceBlock {
	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IRubyFoldingPreferences#createControl(org.eclipse.swt.widgets.Group)
	 */
	public Control createControl(Composite composite) {
		Composite inner= new Composite(composite, SWT.NONE);
		inner.setLayout(new GridLayout(3, false));

		Label label= new Label(inner, SWT.CENTER);
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.widthHint= 30;
		label.setLayoutData(gd);
		
		label= new Label(inner, SWT.CENTER);
		label.setText(FoldingMessages.EmptyRubyFoldingPreferenceBlock_emptyCaption);
		gd= new GridData(GridData.CENTER);
		label.setLayoutData(gd);

		label= new Label(inner, SWT.CENTER);
		gd= new GridData(GridData.FILL_BOTH);
		gd.widthHint= 30;
		label.setLayoutData(gd);
		
		return inner;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IRubyFoldingPreferenceBlock#initialize()
	 */
	public void initialize() {
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IRubyFoldingPreferenceBlock#performOk()
	 */
	public void performOk() {
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IRubyFoldingPreferenceBlock#performDefaults()
	 */
	public void performDefaults() {
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IRubyFoldingPreferenceBlock#dispose()
	 */
	public void dispose() {
	}

}
