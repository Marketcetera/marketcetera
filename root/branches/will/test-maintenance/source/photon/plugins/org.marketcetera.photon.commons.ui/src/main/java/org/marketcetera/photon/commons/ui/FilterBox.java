package org.marketcetera.photon.commons.ui;

import java.util.EventObject;

import net.miginfocom.swt.MigLayout;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Text box that emulates SWT.SEARCH | SWT.CANCEL on platforms that don't
 * support it. This was based on code from the enhanced Eclipse 3.5
 * org.eclipse.ui.dialogs.FilteredTree.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class FilterBox extends Composite {

	/**
	 * Image descriptor for enabled clear button.
	 */
	private static final String CLEAR_ICON = "org.marketcetera.photon.internal.commons.ui.CLEAR_ICON"; //$NON-NLS-1$

	/**
	 * Image descriptor for disabled clear button.
	 */
	private static final String DCLEAR_ICON = "org.marketcetera.photon.internal.commons.ui.DCLEAR_ICON"; //$NON-NLS-1$

	/**
	 * Indicates if the platform natively supports a cancelable search widget
	 */
	protected static Boolean useNativeSearchField;

	private static void testNativeSearchField(Composite composite) {
		if (useNativeSearchField == null) {
			useNativeSearchField = false;
			Text testText = null;
			try {
				testText = new Text(composite, SWT.SEARCH | SWT.CANCEL);
				if ((testText.getStyle() & SWT.CANCEL) != 0) {
					useNativeSearchField = Boolean.TRUE;
				} else {
					ImageDescriptor descriptor = AbstractUIPlugin
							.imageDescriptorFromPlugin(
									"org.marketcetera.photon.commons.ui", //$NON-NLS-1$
									"$nl$/icons/full/etool16/clear_co.gif"); //$NON-NLS-1$
					if (descriptor != null) {
						JFaceResources.getImageRegistry().put(CLEAR_ICON,
								descriptor);
					}
					descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
							"org.marketcetera.photon.commons.ui", //$NON-NLS-1$ 
							"$nl$/icons/full/dtool16/clear_co.gif"); //$NON-NLS-1$
					if (descriptor != null) {
						JFaceResources.getImageRegistry().put(DCLEAR_ICON,
								descriptor);
					}
				}
			} finally {
				if (testText != null) {
					testText.dispose();
				}
			}
		}
	}

	/**
	 * The Composite on which the filter controls are created. This is used to
	 * set the background color of the filter controls to match the surrounding
	 * controls.
	 */
	protected Composite filterComposite;

	/**
	 * The filter text widget to be used by this tree. This value may be
	 * <code>null</code> if there is no filter widget, or if the controls have
	 * not yet been created.
	 */
	protected Text filterText;

	/**
	 * The control representing the clear button for the filter text entry. This
	 * value may be <code>null</code> if no such button exists, or if the
	 * controls have not yet been created.
	 * <p>
	 * <strong>Note:</strong> This is only used if the platform does not
	 * natively support cancel text boxes.
	 * </p>
	 */
	protected Control clearButtonControl;

	/**
	 * The text to initially show in the filter text control.
	 */
	protected String initialText = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            parent composite in which to create the control
	 */
	public FilterBox(Composite parent) {
		super(parent, SWT.NONE);
		testNativeSearchField(this);
		init(parent);
	}

	/**
	 * Create the control.
	 */
	protected void init(Composite parent) {
		setFont(parent.getFont());
		setLayout(new MigLayout("ins 0, fill")); //$NON-NLS-1$
		createFilterControls(this).setLayoutData("dock center"); //$NON-NLS-1$
	}

	protected Composite createFilterControls(Composite parent) {
		Composite composite;
		if (useNativeSearchField) {
			composite = new Composite(parent, SWT.NONE);
		} else {
			composite = new Composite(parent, SWT.BORDER);
			composite.setBackground(getDisplay().getSystemColor(
					SWT.COLOR_LIST_BACKGROUND));
		}
		composite.setLayout(new MigLayout("ins 0, fill")); //$NON-NLS-1$
		filterText = createFilterText(composite);
		filterText.setLayoutData("w 150"); //$NON-NLS-1$
		// only create the button if the text widget doesn't support one
		// natively
		if ((filterText.getStyle() & SWT.CANCEL) == 0) {
			clearButtonControl = createClearControl(composite);
			// initially there is no text to clear
			clearButtonControl.setVisible(false);
		}
		return composite;
	}

	protected Text createFilterText(Composite parent) {
		final Text text = useNativeSearchField ? new Text(parent, SWT.SINGLE
				| SWT.BORDER | SWT.SEARCH | SWT.CANCEL) : new Text(parent,
				SWT.SINGLE);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				/*
				 * Running in an asyncExec because the selectAll() does not
				 * appear to work when using mouse to give focus to text.
				 */
				Display display = filterText.getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						if (!filterText.isDisposed()) {
							if (getInitialText().equals(
									filterText.getText().trim())) {
								filterText.selectAll();
							}
						}
					}
				});
			}
		});

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});

		// if we're using a field with built in cancel we need to listen for
		// default selection changes (which tell us the cancel button has been
		// pressed)
		if ((text.getStyle() & SWT.CANCEL) != 0) {
			text.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					if (e.detail == SWT.CANCEL)
						clearText();
				}
			});
		}
		return text;
	}

	protected Control createClearControl(Composite composite) {
		final Image inactiveImage = JFaceResources.getImageRegistry()
				.getDescriptor(DCLEAR_ICON).createImage();
		final Image activeImage = JFaceResources.getImageRegistry()
				.getDescriptor(CLEAR_ICON).createImage();

		final Label clearButton = new Label(composite, SWT.NONE);
		clearButton.setImage(inactiveImage);
		clearButton.setBackground(composite.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));
		clearButton.setToolTipText(Messages.FilterBox_clearButton_tooltip);
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				clearText();
			}
		});
		clearButton.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				clearButton.setImage(activeImage);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				clearButton.setImage(inactiveImage);
			}
		});
		clearButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				inactiveImage.dispose();
				activeImage.dispose();
			}
		});
		return clearButton;
	}

	/**
	 * Update the receiver after the text has changed.
	 */
	protected void textChanged() {
		String text = getFilterString();
		if (text != null && text.length() > 0 && !text.equals(initialText)) {
			clearButtonControl.setVisible(true);
			fireFilterChange(new FilterChangeEvent(text));
		} else {
			clearButtonControl.setVisible(false);
			fireFilterChange(new FilterChangeEvent("")); //$NON-NLS-1$
		}
	}
	
	public String getFilterText() {
		String text = getFilterString();
		if (text != null && text.length() > 0 && !text.equals(initialText)) {
			return text;
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Set the text that will be shown until the first focus. A default value is
	 * provided, so this method only need be called if overriding the default
	 * initial text is desired.
	 * 
	 * @param text
	 *            initial text to appear in text field
	 */
	public void setInitialText(String text) {
		initialText = text;
		setFilterText(initialText);
		textChanged();
	}

	/**
	 * Clears the text in the filter text widget.
	 */
	protected void clearText() {
		setFilterText(""); //$NON-NLS-1$
		textChanged();
	}

	/**
	 * Set the text in the filter control.
	 * 
	 * @param string
	 *            the text to set
	 */
	protected void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}

	/**
	 * Select all text in the filter text field.
	 */
	protected void selectAll() {
		if (filterText != null) {
			filterText.selectAll();
		}
	}

	/**
	 * Get the filter text for the receiver, if it was created. Otherwise return
	 * <code>null</code>.
	 * 
	 * @return the filter Text, or null if it was not created
	 */
	public Text getFilterControl() {
		return filterText;
	}

	/**
	 * Convenience method to return the text of the filter control. If the text
	 * widget is not created, then null is returned.
	 * 
	 * @return String in the text, or null if the text does not exist
	 */
	protected String getFilterString() {
		return filterText != null ? filterText.getText() : null;
	}

	/**
	 * Get the initial text for the receiver.
	 * 
	 * @return String
	 */
	protected String getInitialText() {
		return initialText;
	}

	/**
	 * Set the background for the widgets that support the filter text area.
	 * 
	 * @param background
	 *            background <code>Color</code> to set
	 */
	public void setBackground(Color background) {
		super.setBackground(background);
		if (filterComposite != null && useNativeSearchField) {
			filterComposite.setBackground(background);
		}
	}

	// Boiler plate listener code

	/**
	 * Keeps track of listeners.
	 */
	private final ListenerList mFilterChangeListeners = new ListenerList(
			ListenerList.IDENTITY);

	/**
	 * Fire a change event to all listeners.
	 * 
	 * @param event
	 *            the change event
	 */
	private void fireFilterChange(FilterChangeEvent event) {
		Object[] listeners = mFilterChangeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((FilterChangeListener) listeners[i]).filterChanged(event);
		}
	}

	/**
	 * Removes a listener. This has no effect if an identical listener already
	 * exists.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addListener(FilterChangeListener listener) {
		mFilterChangeListeners.add(listener);
	}

	/**
	 * Removes a listener. This has no effect if the listener does not exist.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(FilterChangeListener listener) {
		mFilterChangeListeners.remove(listener);
	}

	/**
	 * Interface to notify listeners of changes to the filter.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.5.0
	 */
	@ClassVersion("$Id$")
	public interface FilterChangeListener {

		/**
		 * Callback for change notification.
		 * 
		 * @param event
		 *            event describing the change
		 */
		void filterChanged(FilterChangeEvent event);
	}

	/**
	 * Event object for {@link FilterChangeListener}.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.5.0
	 */
	@ClassVersion("$Id$")
	public class FilterChangeEvent extends EventObject {

		private static final long serialVersionUID = 1L;
		private String filterText;

		/**
		 * Constructor.
		 * 
		 * @param filterText
		 */
		private FilterChangeEvent(String filterText) {
			super(FilterBox.this);
			this.filterText = filterText;
		}

		/**
		 * Returns the filter text that was a result of this event.
		 * 
		 * @return the filter text as a result of this event
		 */
		public String getFilterText() {
			return filterText;
		}

	}

}
