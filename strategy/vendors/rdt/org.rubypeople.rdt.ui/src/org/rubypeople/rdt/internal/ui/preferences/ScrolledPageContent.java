/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

/**
 * see bug 79141 
 */
public class ScrolledPageContent extends SharedScrolledComposite {

	private KeyboardHandler fKeyboardHandler;
	private VisibilityHandler fVisibilityHandler;

	private class VisibilityHandler extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			Widget w = e.widget;
			if (w instanceof Control) {
				FormUtil.ensureVisible(ScrolledPageContent.this, (Control) w);
			}
		}
	}
	private class KeyboardHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			Widget w = e.widget;
			if (w instanceof Control) {
				if (e.doit)
					FormUtil.processKey(e.keyCode, ScrolledPageContent.this);
			}
		}
	}
	
	public ScrolledPageContent(Composite parent) {
		this(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}
	
	public ScrolledPageContent(Composite parent, int style) {
		super(parent, style);
		setExpandHorizontal(true);
		setExpandVertical(true);
		setContent(new Composite(this, SWT.NONE));
		fVisibilityHandler= new VisibilityHandler();
		fKeyboardHandler= new KeyboardHandler();
		addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.Activate) {
					forceFocus();
				}
			}
		});
	}
	
	public void adaptChild(Control childControl) {
		childControl.addKeyListener(fKeyboardHandler);
		childControl.addFocusListener(fVisibilityHandler);
	}
	
	public Composite getBody() {
		return (Composite) getContent();
	}

}
