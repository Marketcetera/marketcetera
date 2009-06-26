package org.marketcetera.photon.ui.databinding;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An abstract superclass for observable values that gurantees that the 
 * observable will be disposed when the control to which it is attached is
 * disposed.
 * 
 * Copied from old Eclipse 3.4 code since we were depending it.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractSWTObservableValue extends AbstractObservableValue implements ISWTObservableValue {

	private final Widget widget;

	/**
	 * Standard constructor for an SWT ObservableValue.  Makes sure that
	 * the observable gets disposed when the SWT widget is disposed.
	 * 
	 * @param widget
	 */
	protected AbstractSWTObservableValue(Widget widget) {
		this(SWTObservables.getRealm(widget.getDisplay()), widget);
	}
	
	/**
	 * Constructor that allows for the setting of the realm. Makes sure that the
	 * observable gets disposed when the SWT widget is disposed.
	 * 
	 * @param realm
	 * @param widget
	 * @since 1.2
	 */
	protected AbstractSWTObservableValue(Realm realm, Widget widget) {
		super(realm);
		this.widget = widget;
		widget.addDisposeListener(disposeListener);
	}
	
	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			AbstractSWTObservableValue.this.dispose();
		}
	};

	/**
	 * @return Returns the widget.
	 */
	public Widget getWidget() {
		return widget;
	}
}
