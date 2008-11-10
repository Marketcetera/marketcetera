package org.rubypeople.rdt.internal.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.rubypeople.rdt.internal.ui.viewsupport.ContainerCheckedTreeViewer;

/**
 * A class to select elements out of a tree structure.
 */
public class CheckedTreeSelectionDialog extends SelectionStatusDialog {

	private CheckboxTreeViewer fViewer;

	private ILabelProvider fLabelProvider;
	private ITreeContentProvider fContentProvider;

	private ISelectionValidator fValidator = null;
	private ViewerSorter fSorter;
	private String fEmptyListMessage = "No entries available";

	private IStatus fCurrStatus = new StatusInfo();
	private List fFilters;
	private Object fInput;
	private boolean fIsEmpty;

	private int fWidth = 60;
	private int fHeight = 18;

	private boolean fContainerMode;
	private Object[] fExpandedElements;

	/**
	 * Constructs an instance of <code>ElementTreeSelectionDialog</code>.
	 * @param labelProvider   the label provider to render the entries
	 * @param contentProvider the content provider to evaluate the tree structure
	 */
	public CheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent);

		fLabelProvider = labelProvider;
		fContentProvider = contentProvider;

		setResult(new ArrayList(0));
		setStatusLineAboveButtons(true);

		fContainerMode = false;
		fExpandedElements = null;

		int shellStyle = getShellStyle();
		setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);
	}

	/**
	 * If set, the checked /gray state of containers (inner nodes) is derived from the checked state of its 
	 * leaf nodes.
	 * @param containerMode The containerMode to set
	 */
	public void setContainerMode(boolean containerMode) {
		fContainerMode = containerMode;
	}

	/**
	 * Sets the initial selection.
	 * Convenience method.
	 * @param selection the initial selection.
	 */
	public void setInitialSelection(Object selection) {
		setInitialSelections(new Object[] { selection });
	}

	/**
	 * Sets the message to be displayed if the list is empty.
	 * @param message the message to be displayed.
	 */
	public void setEmptyListMessage(String message) {
		fEmptyListMessage = message;
	}

	/**
	 * Sets the sorter used by the tree viewer.
	 */
	public void setSorter(ViewerSorter sorter) {
		fSorter = sorter;
	}

	/**
	 * Adds a filter to the tree viewer.
	 * @param filter a filter.
	 */
	public void addFilter(ViewerFilter filter) {
		if (fFilters == null)
			fFilters = new ArrayList(4);

		fFilters.add(filter);
	}

	/**
	 * Sets an optional validator to check if the selection is valid.
	 * The validator is invoked whenever the selection changes.
	 * @param validator the validator to validate the selection.
	 */
	public void setValidator(ISelectionValidator validator) {
		fValidator = validator;
	}

	/**
	 * Sets the tree input.
	 * @param input the tree input.
	 */
	public void setInput(Object input) {
		fInput = input;
	}

	/**
	 * Expands the tree
	 */
	public void setExpandedElements(Object[] elements) {
		fExpandedElements = elements;
	}

	/**
	 * Sets the size of the tree in unit of characters.
	 * @param width  the width of the tree.
	 * @param height the height of the tree.
	 */
	public void setSize(int width, int height) {
		fWidth = width;
		fHeight = height;
	}

	protected void updateOKStatus() {
		if (!fIsEmpty) {
			if (fValidator != null) {
				fCurrStatus = fValidator.validate(fViewer.getCheckedElements());
				updateStatus(fCurrStatus);
			} else if (!fCurrStatus.isOK()) {
				fCurrStatus = new StatusInfo();
			}
		} else {
			fCurrStatus = new StatusInfo(IStatus.ERROR, fEmptyListMessage);
		}
		updateStatus(fCurrStatus);
	}

	/*
	 * @see Window#open()
	 */
	public int open() {
		fIsEmpty = evaluateIfTreeEmpty(fInput);
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				access$superOpen();
			}
		});

		return getReturnCode();
	}

	private void access$superOpen() {
		super.open();
	}

	/**
	 * Handles cancel button pressed event.
	 */
	protected void cancelPressed() {
		setResult(null);
		super.cancelPressed();
	}

	/*
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
		setResult(Arrays.asList(fViewer.getCheckedElements()));
	}

	/*
	 * @see Window#create()
	 */
	public void create() {
		super.create();

		List initialSelections = getInitialElementSelections();
		if (initialSelections != null) {
			fViewer.setCheckedElements(initialSelections.toArray());
		}

		if (fExpandedElements != null) {
			fViewer.setExpandedElements(fExpandedElements);
		}

		updateOKStatus();
	}

	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Label messageLabel = createMessageArea(composite);
		Control treeWidget = createTreeViewer(composite);
		Control buttonComposite = createSelectionButtons(composite);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(fWidth);
		data.heightHint = convertHeightInCharsToPixels(fHeight);
		treeWidget.setLayoutData(data);

		if (fIsEmpty) {
			messageLabel.setEnabled(false);
			treeWidget.setEnabled(false);
			buttonComposite.setEnabled(false);
		}

		return composite;
	}

	private Tree createTreeViewer(Composite parent) {
		if (fContainerMode) {
			fViewer = new ContainerCheckedTreeViewer(parent, SWT.BORDER);
		} else {
			fViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
		}

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(fLabelProvider);
		fViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateOKStatus();
			}
		});

		fViewer.setSorter(fSorter);
		if (fFilters != null) {
			for (int i = 0; i != fFilters.size(); i++)
				fViewer.addFilter((ViewerFilter) fFilters.get(i));
		}

		fViewer.setInput(fInput);

		return fViewer.getTree();
	}

	/**
	 * Add the selection and deselection buttons to the dialog.
	 * @param composite org.eclipse.swt.widgets.Composite
	 */
	private Composite createSelectionButtons(Composite composite) {

		Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		composite.setData(data);

		Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, "Select &All", false);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fViewer.setCheckedElements(fContentProvider.getElements(fInput));
				updateOKStatus();
			}
		};
		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, "&Deselect All", false);

		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fViewer.setCheckedElements(new Object[0]);
				updateOKStatus();
			}
		};
		deselectButton.addSelectionListener(listener);
		return buttonComposite;
	}

	private boolean evaluateIfTreeEmpty(Object input) {
		Object[] elements = fContentProvider.getElements(input);
		if (elements.length > 0) {
			if (fFilters != null) {
				for (int i = 0; i < fFilters.size(); i++) {
					ViewerFilter curr = (ViewerFilter) fFilters.get(i);
					elements = curr.filter(fViewer, input, elements);
				}
			}
		}
		return elements.length == 0;
	}

}