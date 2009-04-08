/*******************************************************************************
 * Copyright (c) 2006, emedia-solutions wolf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     emedia-solutions wolf - initial API and implementation 
 *                             (inspired by Phillip Bairds ideas on the 
 *                             XSWT mailinglist)
 ******************************************************************************/
package com.swtworkbench.community.xswt.layoutbuilder;


import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;

/**
 * @author markusw
 * @version 1.0
 * 
 */
public class FormToolkitLayoutBuilder extends SWTLayoutBuilder {

	/**
	 * Toolkit to use for this
	 * {@link com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder}
	 */
	private FormToolkit toolkit;

	/**
	 * @param xswt
	 */
	public FormToolkitLayoutBuilder(XSWT xswt) {
		super(xswt);
	}

	/**
	 * Constructor with a precreated
	 * {@link org.eclipse.ui.forms.widgets.FormToolkit}.
	 * 
	 * @param xswt
	 * @param toolkit
	 */
	public FormToolkitLayoutBuilder(XSWT xswt, FormToolkit toolkit) {
		this(xswt);
		this.toolkit = toolkit;
	}

	private void createToolkit(Composite parent) {
		this.toolkit = new FormToolkit(parent.getDisplay());
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeToolkit();
			}
		});
	}

	final void disposeToolkit() {
		this.toolkit.dispose();
		this.toolkit = null;
	}

	/**
	 * @return the toolkit
	 */
	public FormToolkit getToolkit() {
		return this.toolkit;
	}

	/**
	 * @see com.swtworkbench.community.xswt.layoutbuilder.SWTLayoutBuilder#construct(java.lang.Class,
	 *      java.lang.Object, int, java.lang.String, java.lang.Object)
	 */
	public Object construct(Class klass, Object parent, int style, String name,
			Object contextElement) throws XSWTException {
		if (parent instanceof Composite) {
			Composite p = (Composite) parent;
			if (this.toolkit == null) {
				this.createToolkit(p);
			}
			if (Button.class.equals(klass)) {
				return this.toolkit.createButton(p, name, style);
			} else if (Composite.class.equals(klass)) {
				return this.toolkit.createComposite(p, style);
			} else if ("CompositeSeparator".equals(klass.getSimpleName())) { //$NON-NLS-1$
				return this.toolkit.createCompositeSeparator(p);
			} else if (ExpandableComposite.class.equals(klass)) {
				return this.toolkit.createExpandableComposite(p, style);
			} else if (Form.class.equals(klass)) {
				Form form = this.toolkit.createForm(p);
				this.toolkit.paintBordersFor(form.getBody());
				return form;
			} else if (FormText.class.equals(klass)) {
				// FIXME: The trackFocus parameter should be configurable via xml
				return this.toolkit.createFormText(p, true);
			} else if (Hyperlink.class.equals(klass)) {
				return this.toolkit.createHyperlink(p, name, style);
			} else if (ImageHyperlink.class.equals(klass)) {
				return this.toolkit.createImageHyperlink(p, style);
			} else if (Label.class.equals(klass)) {
				return this.toolkit.createLabel(p, name, style);
			} else if (ScrolledForm.class.equals(klass)) {
				ScrolledForm form = this.toolkit.createScrolledForm(p);
				this.toolkit.paintBordersFor(form.getBody());
				return form;
			} else if (ScrolledPageBook.class.equals(klass)) {
				return this.toolkit.createPageBook(p, style);
			} else if (Section.class.equals(klass)) {
				return this.toolkit.createSection(p, style);
			} else if ("Separator".equals(klass.getSimpleName())) { //$NON-NLS-1$
				return this.toolkit.createSeparator(p, style);
			} else if (Table.class.equals(klass)) {
				return this.toolkit.createTable(p, style);
			} else if (Text.class.equals(klass)) {
				return this.toolkit.createText(p, name, style);
			} else if (Tree.class.equals(klass)) {
				return this.toolkit.createTree(p, style);
			}
		}
		// Fall back
		Object constructed = super.construct(klass, parent, style, name,
				contextElement);
		if (constructed instanceof Control) {
			Control constructedControl = (Control) constructed;
			this.toolkit.adapt(constructedControl, false, false);
		}
		return constructed;
	}

}

