package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.EclipseUtils;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.layoutbuilder.FormToolkitLayoutBuilder;

/**
 * Abstract base class for Eclipse views built from an XSWT descriptor file.
 * This class takes care of instantiating the UI, setting up the databinding 
 * context, and provides some utility classes for "finishing" the UI.
 * 
 * The type parameter indicates the type of object that should be returned
 * from the call to {@link XSWT#parse(Object, Class)}, and should correspond
 * to the value returned by {@link #getXSWTInterfaceClass()}.
 * 
 * 
 * Subclassers should implement the {@link #finishUI()} callback in order
 * to perform any "cleanup" work necessary after the XSWT file has been loaded
 * and instantiated, for example hooking up event listeners.
 * 
 * @author gmiller
 *
 * @param <T>
 */
public abstract class XSWTView<T> extends ViewPart {


	private DataBindingContext dataBindingContext;
	private T xswtView;

	@Override
	public void dispose() {
		dataBindingContext.dispose();
		super.dispose();
	}
	
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public void createPartControl(Composite parent) {
		try {
			XSWT xswt = XSWT.create(getXSWTResourceStream());
			new FormToolkitLayoutBuilder(xswt);
			xswtView = (T)xswt.parse(parent, getXSWTInterfaceClass());
			dataBindingContext = new DataBindingContext();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
		finishUI();
	}

	/**
	 * Get the class of the interface type that should be returned
	 * from {@link XSWT#parse(Object, Class)}
	 * 
	 * @return the interface class
	 */
	protected abstract Class<? extends T> getXSWTInterfaceClass();

	/**
	 * Subclasses are recommended to "post-process" the UI,
	 * in order to take care of tasks like event listener connection,
	 * fine-tuning of UI components, and other tasks that require
	 * a real programming language (not XSWT).
	 */
	protected abstract void finishUI();

	/**
	 * The input stream from which to load the XSWT xml
	 * file.
	 * 
	 * @return the input stream
	 */
	protected abstract InputStream getXSWTResourceStream();

	/**
	 * This utility method sets the "size" hint of a control
	 * based on the size of the specified number of characters in the
	 * default font for the control.
	 * 
	 * @param aControl the control to size
	 * @param charSize the recommended width in characters
	 */
	protected void updateSize(Control aControl, int charSize) {
		Point sizeHint = EclipseUtils.getTextAreaSize(aControl,
				null, charSize, 1.0);
		Object layoutData = aControl.getLayoutData();
		if (layoutData instanceof GridData){
			((GridData)layoutData).widthHint = sizeHint.x;
			((GridData)layoutData).heightHint = sizeHint.y;
		} else if (layoutData instanceof RowData){
			((RowData)layoutData).width = sizeHint.x;
			((RowData)layoutData).height = sizeHint.y;
		}
	}

	/**
	 * This utility method loops through the columns in a table
	 * and calls {@link TableColumn#pack()}
	 * @param table the table to pack
	 */
	public static void packColumns(Table table) {
		for (TableColumn column : table.getColumns()) {
			column.pack();
		}
	}


	/**
	 * Return the {@link DataBindingContext} for this view.
	 * @return the DataBindingContext
	 */
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}
	
	/**
	 * Clear the {@link DataBindingContext} for this view of all bindings.
	 */
	public void clearDataBindingContext() {
		Realm origRealm = dataBindingContext.getValidationRealm();
		try {
			dataBindingContext.dispose();
		} catch (Exception ex){
		}
		dataBindingContext = new DataBindingContext(origRealm);
	}

	/**
	 * Return the XSWT proxy object.
	 * 
	 * @return the XSWT proxy object
	 */
	public T getXSWTView() {
		return xswtView;
	}
	
}
