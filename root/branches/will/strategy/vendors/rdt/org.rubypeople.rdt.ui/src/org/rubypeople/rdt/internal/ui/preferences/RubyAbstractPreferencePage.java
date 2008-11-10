/*
 * Created on Jun 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.ui.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * @author markus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class RubyAbstractPreferencePage extends PreferencePage {

	protected OverlayPreferenceStore fOverlayStore;
	protected Map fCheckBoxes = new HashMap();
	private SelectionListener fCheckBoxListener = new SelectionListener() {
	
			public void widgetDefaultSelected(SelectionEvent e) {}
	
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				fOverlayStore.setValue((String) fCheckBoxes.get(button), button.getSelection());
			}
		};

	protected Button addCheckBox(Composite parent, String label, String key, int indentation) {
		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText(label);
	
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = indentation;
		gd.horizontalSpan = 2;
		checkBox.setLayoutData(gd);
		checkBox.addSelectionListener(fCheckBoxListener);
	
		fCheckBoxes.put(checkBox, key);
	
		return checkBox;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return RubyPlugin.getDefault().getPreferenceStore();
	}

	protected Map fTextFields = new HashMap();

	protected void initializeFields() {
	
		Iterator e = fCheckBoxes.keySet().iterator();
		while (e.hasNext()) {
			Button b = (Button) e.next();
			String key = (String) fCheckBoxes.get(b);
			b.setSelection(fOverlayStore.getBoolean(key));
		}
	
		e = fTextFields.keySet().iterator();
		while (e.hasNext()) {
			Text t = (Text) e.next();
			String key = (String) fTextFields.get(t);
			t.setText(fOverlayStore.getString(key));
		}
	
	}

}
