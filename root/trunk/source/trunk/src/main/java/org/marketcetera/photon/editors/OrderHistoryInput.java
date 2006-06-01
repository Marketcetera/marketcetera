package org.marketcetera.photon.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.marketcetera.photon.model.DBFIXMessageHistory;
import org.marketcetera.photon.model.FIXMessageHistory;

public class OrderHistoryInput implements IEditorInput {

	DBFIXMessageHistory history;
	public OrderHistoryInput(DBFIXMessageHistory history){
		this.history = history;
	}
	
	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Order History";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Order History";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public boolean equals(Object obj){
		if (super.equals(obj))
			return true;
		if (!(obj instanceof OrderHistoryInput))
			return false;
		return true;
	}
	public int hashcode() {
		return this.getClass().getCanonicalName().hashCode();
	}

	/**
	 * @return Returns the history.
	 */
	public DBFIXMessageHistory getHistory() {
		return history;
	}
}