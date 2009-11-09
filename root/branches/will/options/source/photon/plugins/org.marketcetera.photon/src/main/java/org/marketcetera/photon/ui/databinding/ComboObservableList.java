package org.marketcetera.photon.ui.databinding;

import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Combo;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Copied from old Eclipse 3.4 code since we were depending it.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ComboObservableList extends SWTObservableList {

	private final Combo combo;

	/**
	 * @param combo
	 */
	public ComboObservableList(Combo combo) {
		super(SWTObservables.getRealm(combo.getDisplay()));
		this.combo = combo;
	}

	protected int getItemCount() {
		return combo.getItemCount();
	}

	protected void setItems(String[] newItems) {
		combo.setItems(newItems);
	}

	protected String[] getItems() {
		return combo.getItems();
	}

	protected String getItem(int index) {
		return combo.getItem(index);
	}

	protected void setItem(int index, String string) {
		combo.setItem(index, string);
	}
}
