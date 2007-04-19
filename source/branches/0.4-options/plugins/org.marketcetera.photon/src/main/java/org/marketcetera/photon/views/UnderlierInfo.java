package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class UnderlierInfo {
	
	public UnderlierInfo(Composite parent)
	{
		createColors(parent);
		createFirstRowComposite(parent);
		createSecondRowComposite(parent);
	}
	
	private FormToolkit formToolkit;
	
	// Dynamiclly-updated controls in first row of the main info section
	private Label instrumentLabel;
	private Label lastPriceUpDownArrowLabel;
	private Label lastPriceLabel;
	private Label lastPriceChangeLabel;
	private Label bidPriceLabel;
	private Label askPriceLabel;
	private Label bidSizeLabel;
	private Label askSizeLabel;

	// Dynamiclly-updated in second row of the main info section	
	private Label lastUpdatedTimeLabel;
	private Label volumeLabel;
	private Label openPriceLabel;
	private Label highPriceLabel;
	private Label lowPriceLabel;
	private Label tradingVolumeLabel;
	
//	private Label dividendsIndicatorTextLabel;
	
	private CCombo exDividendsDateAndAmount;

	
	private static Color SYSTEM_COLOR_BLUE;
	private static Color SYSTEM_COLOR_RED;
	
	private void createColors(Composite parent)
	{
		Display display = parent.getDisplay();
		SYSTEM_COLOR_BLUE = display.getSystemColor(SWT.COLOR_BLUE);
		SYSTEM_COLOR_RED = display.getSystemColor(SWT.COLOR_RED);
	}
	
	public void dispose()
	{
		SYSTEM_COLOR_BLUE.dispose();
		SYSTEM_COLOR_RED.dispose();		
	}
	
	private void createFirstRowComposite(Composite parent)
	{	
		Composite firstRow = getFormToolkit().createComposite(parent);
		GridLayout gridLayout = createBasicGridLayout(12);
		firstRow.setLayout(gridLayout);
		firstRow.setLayoutData(createHorizontallySpannedGridData());

		// Controls in first row of the main info section
		instrumentLabel = getFormToolkit().createLabel(firstRow, null);
		instrumentLabel.setLayoutData(createFirstRowLabelGridData());

		lastPriceUpDownArrowLabel = getFormToolkit().createLabel(
				firstRow, null);
		lastPriceUpDownArrowLabel.setLayoutData(createNarrowGridData());

		lastPriceLabel = getFormToolkit().createLabel(firstRow, null);
		lastPriceLabel.setForeground(SYSTEM_COLOR_RED);
		lastPriceLabel.setLayoutData(createFirstRowLabelGridData());

		lastPriceChangeLabel = getFormToolkit().createLabel(firstRow, null);		
		lastPriceChangeLabel.setLayoutData(createFirstRowLabelGridData());

		bidPriceLabel = getFormToolkit().createLabel(firstRow, null);
		bidPriceLabel.setLayoutData(createNarrowGridData());
		
		Label bidAskPriceSeparatorLabel = getFormToolkit().createLabel(firstRow, "/");
		bidAskPriceSeparatorLabel.setForeground(SYSTEM_COLOR_BLUE);
		bidAskPriceSeparatorLabel.setLayoutData(createNarrowGridData());


		askPriceLabel = getFormToolkit().createLabel(firstRow, null);
		askPriceLabel.setLayoutData(createFirstRowLabelGridData());

		bidSizeLabel = getFormToolkit().createLabel(firstRow, null);
		bidSizeLabel.setLayoutData(createNarrowGridData());

		Label bidAskSizeSeparatorLabel = getFormToolkit().createLabel(firstRow, "x");
		bidAskSizeSeparatorLabel.setForeground(SYSTEM_COLOR_BLUE);
		bidAskSizeSeparatorLabel.setLayoutData(createNarrowGridData());

		askSizeLabel = getFormToolkit().createLabel(firstRow, null);
		askSizeLabel.setLayoutData(createFirstRowLabelGridData());

		Label exDividendsDateAndAmountLabel = getFormToolkit().createLabel(firstRow, "Divs:");
		exDividendsDateAndAmountLabel.setForeground(SYSTEM_COLOR_BLUE);
		exDividendsDateAndAmountLabel.setLayoutData(createNarrowGridData());

		exDividendsDateAndAmount = new CCombo(firstRow, SWT.BORDER);
		GridData exDividendGridData = createFirstRowLabelGridData();
		exDividendGridData.horizontalAlignment = SWT.END;
		exDividendGridData.minimumWidth = 100;
		exDividendGridData.widthHint = 100;		
		exDividendsDateAndAmount.setLayoutData(exDividendGridData);
		
	}

	private void createSecondRowComposite(Composite parent)
	{	
		Composite secondRow = getFormToolkit().createComposite(parent);
		GridLayout gridLayout = createBasicGridLayout(12);
		secondRow.setLayout(gridLayout);
		secondRow.setLayoutData(createHorizontallySpannedGridData());


		Label timeIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "AT");
		timeIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		timeIndicatorTextLabel.setLayoutData(createNarrowGridData());

		lastUpdatedTimeLabel = getFormToolkit().createLabel(
				secondRow, null);
		lastUpdatedTimeLabel.setLayoutData(createSecondRowLabelGridData());
		
		Label volumeIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Vol");
		volumeIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		volumeIndicatorTextLabel.setLayoutData(createNarrowGridData());

		volumeLabel = getFormToolkit().createLabel(secondRow, null);
		volumeLabel.setLayoutData(createSecondRowLabelGridData());
		
		Label openPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Op");
		openPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		openPriceIndicatorTextLabel.setLayoutData(createNarrowGridData());

		openPriceLabel = getFormToolkit().createLabel(secondRow, null);
		openPriceLabel.setLayoutData(createSecondRowLabelGridData());

		Label highPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Hi");
		highPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		highPriceIndicatorTextLabel.setLayoutData(createNarrowGridData());

		highPriceLabel = getFormToolkit().createLabel(secondRow, null);
		highPriceLabel.setLayoutData(createSecondRowLabelGridData());

		Label lowPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Lo");
		lowPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		lowPriceIndicatorTextLabel.setLayoutData(createNarrowGridData());

		lowPriceLabel = getFormToolkit().createLabel(secondRow, null);
		lowPriceLabel.setLayoutData(createSecondRowLabelGridData());

		Label tradingVolumeIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Trd");
		tradingVolumeIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		tradingVolumeIndicatorTextLabel.setLayoutData(createNarrowGridData());

		tradingVolumeLabel = getFormToolkit().createLabel(secondRow, null);
		tradingVolumeLabel.setLayoutData(createSecondRowLabelGridData());

	}

	// todo: Duplicated code from StockOrderTicket
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

	//todo: duplicate
	private GridData createHorizontallySpannedGridData()
	{
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = SWT.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = SWT.FILL;
		return formGridData;
	}

	private GridData createFirstRowLabelGridData()
	{
		GridData formGridData = new GridData();
		formGridData.minimumWidth = 50;
		formGridData.widthHint = 50;
		formGridData.horizontalAlignment = SWT.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = SWT.FILL;
		return formGridData;
	}

	private GridData createSecondRowLabelGridData()
	{
		GridData formGridData = new GridData();
		formGridData.minimumWidth = 60;
		formGridData.widthHint = 60;
		formGridData.horizontalAlignment = SWT.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = SWT.FILL;
		return formGridData;
	}

	private GridData createNarrowGridData()
	{
		GridData formGridData = new GridData();
		formGridData.horizontalAlignment = SWT.BEGINNING;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = SWT.FILL;
		return formGridData;
	}

	//todo: duplicate
	private GridLayout createBasicGridLayout(int numColumns)
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 0;
		return gridLayout;
	}

	
	public Label getAskPriceLabel() {
		return askPriceLabel;
	}
	public void setAskPriceLabelText(String askPriceLabelText) {
		this.askPriceLabel.setText(askPriceLabelText);
	}
	public void setAskSizeLabelText(String askSizeLabelText) {
		this.askSizeLabel.setText(askSizeLabelText);
	}
	public Label getBidPriceLabel() {
		return bidPriceLabel;
	}
	public void setBidPriceLabelText(String bidPriceLabelText) {
		this.bidPriceLabel.setText(bidPriceLabelText);
	}
	public Label getBidSizeLabel() {
		return bidSizeLabel;
	}
	public void setBidSizeLabelText(String bidSizeLabelText) {
		this.bidSizeLabel.setText(bidSizeLabelText);
	}
	public Label getHighPriceLabel() {
		return highPriceLabel;
	}
	public void setHighPriceLabelText(String highPriceLabelText) {
		this.highPriceLabel.setText(highPriceLabelText);
	}
	public Label getInstrumentLabel() {
		return instrumentLabel;
	}
	public void setInstrumentLabelText(String instrumentLabelText) {
		this.instrumentLabel.setText(instrumentLabelText);
	}
	public Label getLastPriceChangeLabel() {
		return lastPriceChangeLabel;
	}
	public void setLastPriceChangeLabelText(String lastPriceChangeLabelText) {
		this.lastPriceChangeLabel.setText(lastPriceChangeLabelText);
	}
	public Label getLastPriceLabel() {
		return lastPriceLabel;
	}
	public void setLastPriceLabelText(String lastPriceLabelText) {
		this.lastPriceLabel.setText(lastPriceLabelText);
	}
	public Label getLastPriceUpDownArrowLabel() {
		return lastPriceUpDownArrowLabel;
	}
	public void setLastPriceUpDownArrowLabelImage(Image arrowImage) {
		this.lastPriceUpDownArrowLabel.setImage(arrowImage);
	}
	
	public Label getLastUpdatedTimeLabel() {
		return lastUpdatedTimeLabel;
	}
	public void setLastUpdatedTimeLabelText(String lastUpdatedTimeLabelText) {
		this.lastUpdatedTimeLabel.setText(lastUpdatedTimeLabelText);
	}
	public Label getLowPriceLabel() {
		return lowPriceLabel;
	}
	public void setLowPriceLabelText(String lowPriceLabelText) {
		this.lowPriceLabel.setText(lowPriceLabelText);
	}
	public Label getOpenPriceLabel() {
		return openPriceLabel;
	}
	public void setOpenPriceLabelText(String openPriceLabelText) {
		this.openPriceLabel.setText(openPriceLabelText);
	}
	public Label getTradingVolumeLabel() {
		return tradingVolumeLabel;
	}
	public void setTradingVolumeLabelText(String tradingVolumeLabelText) {
		this.tradingVolumeLabel.setText(tradingVolumeLabelText);
	}
	public Label getVolumeLabel() {
		return volumeLabel;
	}
	public void setVolumeLabelText(String volumeLabelText) {
		this.volumeLabel.setText(volumeLabelText);
	}

	public void setExDividendsDateAndAmountItems(String[] dateAmountStrings) {
		this.exDividendsDateAndAmount.setItems(dateAmountStrings);
		if (dateAmountStrings != null && dateAmountStrings.length > 0)
			this.exDividendsDateAndAmount.setText(dateAmountStrings[0]);
	}

}