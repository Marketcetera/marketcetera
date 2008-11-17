package org.marketcetera.photon.ui.databinding;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;

/**
 * This class is quite a HACK, to get around the fact that when you call 
 * {@link Combo#setItems(String[])} on a combo (at least in a Windows environment)
 * the selected text disappears.
 * 
 * The {@link #setItems(String[])} method is overridden to retain the text in 
 * the combo.  (And then reset the selection point).
 * 
 * @author gmiller
 *
 */
@SuppressWarnings("restriction")
public class RetainTextObservable extends org.eclipse.jface.internal.databinding.swt.ComboObservableList {

	private Combo combo;


	public RetainTextObservable(Combo combo) {
		super(combo);
		this.combo = combo;
	}


	/**
	 * Retain the text in the combo while setting the new items.
	 * Also reset the insertion point to the end of the field.
	 * @param newItems
	 */
	protected void setItems(String[] newItems){
		String oldText = combo.getText();
		super.setItems(newItems);
		combo.setText(oldText);
		combo.setSelection(new Point(oldText.length(), oldText.length()));
	}

	
}
