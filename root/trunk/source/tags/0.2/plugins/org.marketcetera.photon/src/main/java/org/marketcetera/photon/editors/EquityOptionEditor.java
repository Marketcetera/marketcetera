package org.marketcetera.photon.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class EquityOptionEditor extends EditorPart {

	private Composite top = null;
	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Composite stockComposite = null;
	private Table optionsTable = null;
	private TableViewer tableViewer = null;
	private Label symbolLabel = null;
	private Label lastLabel = null;
	private Label absChangeLabel = null;
	private Label pctChangeLabel = null;
	private Label bidLabel = null;
	private Label dashLabel = null;
	private Label askLabel = null;
	private Label bidSizeLabel = null;
	private Label xLabel = null;
	private Label askSizeLabel = null;
	private Composite modelComposite;
	private Composite interestDividendComposite = null;
	private ExpandableComposite dividendExpandableComposite = null;
	private ExpandableComposite interestExpandableComposite = null;
	private ExpandableComposite volatilityExpandableComposite = null;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.horizontalSpacing = 2;
        gridLayout1.marginWidth = 2;
        gridLayout1.marginHeight = 2;
        gridLayout1.verticalSpacing = 2;
        top = new Composite(parent, SWT.NONE);
        createStockComposite();
        top.setLayout(gridLayout1);
        optionsTable = getFormToolkit().createTable(top, SWT.NONE);
        optionsTable.setHeaderVisible(true);
        optionsTable.setLinesVisible(true);
        tableViewer = new TableViewer(optionsTable);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	/**
	 * This method initializes stockComposite	
	 *
	 */
	private void createStockComposite() {
		modelComposite = getFormToolkit().createComposite(top);
		modelComposite.setLayout(new RowLayout(SWT.VERTICAL));
		stockComposite = getFormToolkit().createComposite(modelComposite);
		stockComposite.setLayout(new RowLayout());
		createInterestDividendComposite();
		symbolLabel = getFormToolkit().createLabel(stockComposite, "IBM");
		lastLabel = getFormToolkit().createLabel(stockComposite, "81.25");
		absChangeLabel = getFormToolkit().createLabel(stockComposite, "-1.85");
		pctChangeLabel = getFormToolkit().createLabel(stockComposite, "(-2.2%)");
		bidLabel = getFormToolkit().createLabel(stockComposite, "81.24");
		dashLabel = getFormToolkit().createLabel(stockComposite, "-");
		askLabel = getFormToolkit().createLabel(stockComposite, "81.26");
		bidSizeLabel = getFormToolkit().createLabel(stockComposite, "800");
		xLabel = getFormToolkit().createLabel(stockComposite, "x");
		askSizeLabel = getFormToolkit().createLabel(stockComposite, "1000");
	}

	/**
	 * This method initializes interestDividendComposite	
	 *
	 */
	private void createInterestDividendComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		interestDividendComposite = getFormToolkit()
				.createComposite(modelComposite);
		interestDividendComposite.setLayout(gridLayout);
		createVolatilityExpandableComposite();
		createInterestExpandableComposite();
		createDividendExpandableComposite();
	}

	/**
	 * This method initializes dividendExpandableComposite	
	 *
	 */
	private void createDividendExpandableComposite() {
		dividendExpandableComposite = getFormToolkit().createExpandableComposite(interestDividendComposite, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		dividendExpandableComposite.setExpanded(true);
		dividendExpandableComposite.setText("Dividends");
	}

	/**
	 * This method initializes interestExpandableComposite	
	 *
	 */
	private void createInterestExpandableComposite() {
		interestExpandableComposite = getFormToolkit().createExpandableComposite(interestDividendComposite, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		interestExpandableComposite.setExpanded(true);
		interestExpandableComposite.setText("Interest Rate Curve");
	}

	/**
	 * This method initializes volatilityExpandableComposite	
	 *
	 */
	private void createVolatilityExpandableComposite() {
		volatilityExpandableComposite = getFormToolkit().createExpandableComposite(
				interestDividendComposite,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		volatilityExpandableComposite.setExpanded(true);
		volatilityExpandableComposite.setText("Volatility Curve");
	}

}
