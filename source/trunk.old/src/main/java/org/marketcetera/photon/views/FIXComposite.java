package org.marketcetera.photon.views;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

import quickfix.Message;


@ClassVersion("$Id$")
public abstract class FIXComposite extends Composite {

	protected final int fixFieldNumber;

	public FIXComposite(Composite arg0, int arg1, FormToolkit toolkit, int fixFieldNumber) {
		super(arg0, arg1);
		this.fixFieldNumber = fixFieldNumber;
		toolkit.adapt(this);
	}

	public void addFocusListener(FocusListener arg0) {
		super.addFocusListener(arg0);
		Control[] children = this.getChildren();
		for (Control control : children) {
			control.addFocusListener(arg0);
		}
	}

	public void addKeyListener(KeyListener arg0) {
		super.addKeyListener(arg0);
		Control[] children = this.getChildren();
		for (Control control : children) {
			control.addKeyListener(arg0);
		}
	}

	public void addMouseListener(MouseListener arg0) {
		super.addMouseListener(arg0);
		Control[] children = this.getChildren();
		for (Control control : children) {
			control.addMouseListener(arg0);
		}
	}

	public void setBackground(Color arg0) {
		super.setBackground(arg0);
		Control[] children = this.getChildren();
		for (Control control : children) {
			control.setBackground(arg0);
		}
	}

	public void setForeground(Color arg0) {
		super.setForeground(arg0);
		Control[] children = this.getChildren();
		for (Control control : children) {
			control.setForeground(arg0);
		}
	}

	public abstract boolean modifyOrder(Message arg0) throws MarketceteraException;

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public abstract boolean setFocus();

	/**
	 * @return Returns the fixFieldNumber.
	 */
	public int getFixFieldNumber() {
		return fixFieldNumber;
	}
	
	public abstract boolean populateFromMessage(Message aMessage);
	
	public abstract void clear();
	
}
