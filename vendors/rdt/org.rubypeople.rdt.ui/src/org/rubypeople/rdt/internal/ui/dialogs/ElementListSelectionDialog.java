package org.rubypeople.rdt.internal.ui.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.util.List;
import java.util.Arrays;
import org.eclipse.jface.viewers.ILabelProvider;

/**
 * A class to select elements out of a list of elements.
 */
public class ElementListSelectionDialog extends AbstractElementListSelectionDialog {
	
	private Object[] fElements;
	
	/**
	 * Creates a list selection dialog.
	 * @param parent   the parent widget.
	 * @param renderer the label renderer.
	 */	
	public ElementListSelectionDialog(Shell parent,	ILabelProvider renderer) {
		super(parent, renderer);
	}

	/**
	 * Sets the elements of the list.
	 * @param elements the elements of the list.
	 */
	public void setElements(Object[] elements) {
		fElements= elements;
	}

	/*
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
		setResult(Arrays.asList(getSelectedElements()));
	}
	
	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite contents= (Composite) super.createDialogArea(parent);
		
		createMessageArea(contents);
		createFilterText(contents);
		createFilteredList(contents);

		setListElements(fElements);

		List initialSelections= getInitialElementSelections();
		if (initialSelections != null)
			setSelection(initialSelections.toArray());
					
		return contents;
	}

}