package org.marketcetera.photon.views;


import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class StockOrderTicket extends ViewPart {

	public static String ID = "org.marketcetera.photon.views.StockOrderTicket";

	private Composite top = null;

	private FormToolkit toolkit;

	private Form form;

	private FIXEnumeratedComposite buySellControl;

	private FIXStringComposite orderQtyControl;

	private FIXStringComposite symbolControl;

	private FIXStringComposite priceControl;

	private FIXEnumeratedComposite timeInForceControl;

	private FIXStringComposite accountControl;

	private Button sendButton;

	private Button cancelButton;

	private ICommandListener commandListener;

	public StockOrderTicket() {
		super();
		commandListener = new ICommandListener() {
			public void commandIssued(CommandEvent evt) {
				handleCommandIssued(evt);
			};
		};
	}

	protected void handleCommandIssued(CommandEvent evt) {
		if (evt.getDestination() == CommandEvent.Destination.EDITOR) {
			asyncPopulateFromMessage(evt.getMessage());
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		// form.setText("Stock Order Ticket");
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);

		// FIXDataDictionaryManager.loadDictionary(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
		DataDictionary dict = FIXDataDictionaryManager.getDictionary();
		buySellControl = new FIXEnumeratedComposite(form.getBody(), SWT.NONE,
				toolkit, Side.FIELD, dict, new String[] { "" + Side.BUY,
						"" + Side.SELL, "" + Side.SELL_SHORT,
						"" + Side.SELL_SHORT_EXEMPT });
		orderQtyControl = new FIXStringComposite(form.getBody(), SWT.NONE,
				toolkit, OrderQty.FIELD, dict);
		toolkit.paintBordersFor(orderQtyControl);
		symbolControl = new FIXStringComposite(form.getBody(), SWT.NONE,
				toolkit, Symbol.FIELD, dict);
		toolkit.paintBordersFor(symbolControl);
		priceControl = new FIXStringComposite(form.getBody(), SWT.NONE,
				toolkit, Price.FIELD, dict);
		toolkit.paintBordersFor(priceControl);
		timeInForceControl = new FIXEnumeratedComposite(form.getBody(),
				SWT.NONE, toolkit, TimeInForce.FIELD, dict, new String[] {
						"" + TimeInForce.DAY,
						"" + TimeInForce.GOOD_TILL_CANCEL,
						"" + TimeInForce.FILL_OR_KILL,
						"" + TimeInForce.IMMEDIATE_OR_CANCEL });
		timeInForceControl.setSelection("" + TimeInForce.DAY, true);
		accountControl = new FIXStringComposite(form.getBody(), SWT.NONE,
				toolkit, Account.FIELD, dict);
		toolkit.paintBordersFor(accountControl);
		Composite okCancelComposite = toolkit.createComposite(form.getBody());
		okCancelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		okCancelComposite.setLayoutData(gd);
		sendButton = toolkit.createButton(okCancelComposite, "Send", SWT.PUSH);
		cancelButton = toolkit.createButton(okCancelComposite, "Cancel",
				SWT.PUSH);
		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleCancel();
			}
		});
		sendButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleSend();
			}
		});
	}

	protected void handleSend() {
       try {
			String orderID = Application.getIDFactory().getNext();
	        Message aMessage = FIXMessageUtil.newLimitOrder(new InternalID(orderID), Side.BUY, BigDecimal.ZERO,
	        		new MSymbol(""), BigDecimal.ZERO, TimeInForce.DAY, null);
	        aMessage.removeField(Side.FIELD);
	        aMessage.removeField(OrderQty.FIELD);
	        aMessage.removeField(Symbol.FIELD);
	        aMessage.removeField(Price.FIELD);
	        aMessage.removeField(TimeInForce.FIELD);
 			populateMessageFromUI(aMessage);
			Application.getOrderManager().handleInternalMessage(aMessage);
		} catch (Exception e) {
			Application.getMainConsoleLogger().error("Error sending order", e);
		}
	}
	
	protected void handleCancel()
	{
		Control[] children = form.getBody().getChildren();
		for (Control control : children) {
			if (control instanceof FIXComposite) {
				FIXComposite composite = (FIXComposite) control;
				composite.clear();
			}
		}
	}

	@Override
	public void setFocus() {
		this.buySellControl.setFocus();
	}

	/**
	 * Disposes the toolkit
	 */
	public void dispose() {
		super.dispose();
	}

	public void populateFromMessage(Message aMessage) {
		Control[] children = form.getBody().getChildren();
		for (Control control : children) {
			if (control instanceof FIXComposite) {
				FIXComposite composite = (FIXComposite) control;
				composite.populateFromMessage(aMessage);
			}
		}
	}
	
	private void populateMessageFromUI(Message aMessage) throws MarketceteraException{
		Control[] children = form.getBody().getChildren();
		for (Control control : children) {
			if (control instanceof FIXComposite) {
				FIXComposite composite = (FIXComposite) control;
				composite.modifyOrder(aMessage);
			}
		}
	}

	public void asyncExec(Runnable runnable) {
		Display display = this.getSite().getShell().getDisplay();

		// If the display is disposed, you can't do anything with it!!!
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}

	protected void asyncPopulateFromMessage(final Message aMessage) {
		asyncExec(new Runnable() {
			public void run() {
				populateFromMessage(aMessage);
			}
		});
	}

	/**
	 * @return Returns the commandListener.
	 */
	public ICommandListener getCommandListener() {
		return commandListener;
	}
}
