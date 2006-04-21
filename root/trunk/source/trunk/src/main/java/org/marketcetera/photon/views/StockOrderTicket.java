package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

public class StockOrderTicket extends ViewPart {

	public static String ID = "org.marketcetera.photon.views.StockOrderTicket";

	private Composite top = null;

	private FormToolkit toolkit;

	private Label label = null;

	private ScrolledForm scrolledForm;

	private Button buyButton;

	private Button sellButton;

	private Button sellShortButton;

	private Button sellExemptButton;

	public StockOrderTicket() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		tableWrapLayout.numColumns = 3;
		scrolledForm = (ScrolledForm) toolkit.createScrolledForm(parent);
		top = scrolledForm.getBody();
		scrolledForm.setText("Equity Order Ticket");

		// BUY/SELL component
		Composite buySellLabelComposite = toolkit.createComposite(top);
		buySellLabelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		Label aLabel = toolkit.createLabel(buySellLabelComposite, "Side:");
		Composite buySellComposite = toolkit.createComposite(buySellLabelComposite);
		buySellComposite.setLayout(new RowLayout(SWT.VERTICAL));
		buyButton = toolkit.createButton(buySellComposite, "&Buy",
						SWT.RADIO);
		sellButton = toolkit.createButton(buySellComposite, "&Sell",
						SWT.RADIO);
		sellShortButton = toolkit.createButton(buySellComposite,
						"Sell Shor&t", SWT.RADIO);
		sellExemptButton = toolkit.createButton(buySellComposite,
						"Sell E&xempt", SWT.RADIO);
		Label testlabel = toolkit.createLabel(top, "asdf");
		
		top.setLayout(tableWrapLayout);
		
		toolkit.paintBordersFor(top);

		{
			ExpandableComposite ec = toolkit.createExpandableComposite(
					top, ExpandableComposite.TREE_NODE);
			ec.setText("Expandable Composite title");
			String ctext = "We will now create a somewhat long text so that "
					+ "we can use it as content for the expandable composite. "
					+ "Expandable composite is used to hide or show the text using the "
					+ "toggle control";
			Label client = toolkit.createLabel(ec, ctext, SWT.WRAP);
			ec.setClient(client);
			TableWrapData td = new TableWrapData();
			td.colspan = 3;
			ec.setLayoutData(td);
			ec.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					scrolledForm.reflow(true);
				}
			});
		}
	}

	@Override
	public void setFocus() {
		buyButton.setFocus();
	}

	/**
	 * Disposes the toolkit
	 */
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

}
