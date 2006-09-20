package org.marketcetera.photon.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class OrderHistorySelectionProvider extends MultiPageSelectionProvider {

	public OrderHistorySelectionProvider(MultiPageEditorPart arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageSelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
	       ISelectionProvider selectionProvider = ((OrderHistoryEditor)getMultiPageEditor()).getActiveSelectionProvider();
            if (selectionProvider != null)
                return selectionProvider.getSelection();
	        return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageSelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
       ISelectionProvider selectionProvider = ((OrderHistoryEditor)getMultiPageEditor()).getActiveSelectionProvider();
       if (selectionProvider != null)
           selectionProvider.setSelection(selection);
	}

}
