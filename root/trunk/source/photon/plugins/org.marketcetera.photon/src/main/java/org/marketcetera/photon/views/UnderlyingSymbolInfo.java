package org.marketcetera.photon.views;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.Messages;

import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.HighPx;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LowPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.OpenClose;
import quickfix.field.SendingTime;
import quickfix.field.Symbol;
import quickfix.field.TotalVolumeTraded;

public class UnderlyingSymbolInfo
    implements Messages
{
	public enum UnderlyingSymbolDataFields implements IFieldIdentifier
	{
		SYMBOL(Symbol.class), 
		LASTPX(LastPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE), 

		// todo:what to subscribe to for LastPxDelta?
		LASTPXDELTA(LastPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE),  
		LASTQTY(LastQty.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE), 
		BID(BidPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		ASK(OfferPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER), 
		BIDSZ(BidSize.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		ASKSZ(OfferSize.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),

		//cl todo:sending time or lastUpdateTime?  also, what should the last field be?
		LASTUPDATEDTIME(SendingTime.class, MDEntryTime.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE), 
		TRADEVOL(TotalVolumeTraded.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME),
		OPENPX(OpenClose.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OPENING_PRICE),
		HI(HighPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADING_SESSION_HIGH_PRICE), 
		LOW(LowPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADING_SESSION_LOW_PRICE);
		//cl todo:implement dividends time/value + trade value
		
		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;

		UnderlyingSymbolDataFields(Class<?> clazz, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this(clazz);
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		UnderlyingSymbolDataFields(Class<?> clazz) {
			name = clazz.getSimpleName();
			try {
				Field fieldField = clazz.getField("FIELD"); //$NON-NLS-1$
				fieldID = (Integer) fieldField.get(null);
			} catch (Throwable t){
				assert(false);
			}
		}

		public String toString() {
			return name;
		}

		public Integer getFieldID() {
			return fieldID;
		}
		
		public Integer getGroupID() {
			return groupID;
		}

		public Integer getGroupDiscriminatorID() {
			return groupDiscriminatorID;
		}

		public Object getGroupDiscriminatorValue() {
			return groupDiscriminatorValue;
		}

	};
	
	public UnderlyingSymbolInfo(Composite parent)
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
	private Label tradeValueLabel;
	
	private Label exDivDateAmountTextLabel;
	private List exDivDateAmount;

	
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
		// note: don't dispose of any system colors - the framework handles it
		// only dispose of colors we create ourselves
	}
	
	private void createFirstRowComposite(Composite parent)
	{			
		Composite firstRow = getFormToolkit().createComposite(parent);
		firstRow.setLayoutData(createHorizontallySpannedGridData());
		firstRow.setLayout(new FormLayout());
		
		// Controls in first row of the main info section
		instrumentLabel = getFormToolkit().createLabel(firstRow, null);
		instrumentLabel.setLayoutData(createLeftMostControlFormData());

		lastPriceUpDownArrowLabel = getFormToolkit().createLabel(
				firstRow, null);
		lastPriceUpDownArrowLabel.setLayoutData(createFormData(instrumentLabel));

		lastPriceLabel = getFormToolkit().createLabel(firstRow, null);
		lastPriceLabel.setForeground(SYSTEM_COLOR_RED);
		lastPriceLabel.setLayoutData(createNarrowFormData(lastPriceUpDownArrowLabel));

		lastPriceChangeLabel = getFormToolkit().createLabel(firstRow, null);		
		lastPriceChangeLabel.setLayoutData(createFormData(lastPriceLabel));

		bidPriceLabel = getFormToolkit().createLabel(firstRow, null);
		bidPriceLabel.setLayoutData(createFormData(lastPriceChangeLabel));
		
		Label bidAskPriceSeparatorLabel = getFormToolkit().createLabel(firstRow, "-"); //$NON-NLS-1$
		bidAskPriceSeparatorLabel.setForeground(SYSTEM_COLOR_BLUE);
		bidAskPriceSeparatorLabel.setLayoutData(createNarrowFormData(bidPriceLabel));


		askPriceLabel = getFormToolkit().createLabel(firstRow, null);
		askPriceLabel.setLayoutData(createNarrowFormData(bidAskPriceSeparatorLabel));

		bidSizeLabel = getFormToolkit().createLabel(firstRow, null);
		bidSizeLabel.setLayoutData(createFormData(askPriceLabel));

		Label bidAskSizeSeparatorLabel = getFormToolkit().createLabel(firstRow, "x"); //$NON-NLS-1$
		bidAskSizeSeparatorLabel.setForeground(SYSTEM_COLOR_BLUE);
		bidAskSizeSeparatorLabel.setLayoutData(createNarrowFormData(bidSizeLabel));

		askSizeLabel = getFormToolkit().createLabel(firstRow, null);
		askSizeLabel.setLayoutData(createNarrowFormData(bidAskSizeSeparatorLabel));

		exDivDateAmountTextLabel = getFormToolkit().createLabel(firstRow, "Divs"); //$NON-NLS-1$
		exDivDateAmountTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		exDivDateAmountTextLabel.setLayoutData(createFormData(askSizeLabel));
		

		exDivDateAmount = new List(firstRow, SWT.V_SCROLL | SWT.H_SCROLL);
		FormData narrowForm = createNarrowFormData(exDivDateAmountTextLabel);
		narrowForm.height = 0;   	
		exDivDateAmount.setLayoutData(narrowForm);		
	}
	
	private void createSecondRowComposite(Composite parent)
	{	
		Composite secondRow = getFormToolkit().createComposite(parent);
		secondRow.setLayoutData(createHorizontallySpannedGridData());
		secondRow.setLayout(new FormLayout());

		Label timeIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "AT "); //$NON-NLS-1$
		timeIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		timeIndicatorTextLabel.setLayoutData(createLeftMostControlFormData());

		lastUpdatedTimeLabel = getFormToolkit().createLabel(
				secondRow, null);
		lastUpdatedTimeLabel.setLayoutData(createNarrowFormData(timeIndicatorTextLabel));
		
		Label volumeIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Vol "); //$NON-NLS-1$
		volumeIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		volumeIndicatorTextLabel.setLayoutData(createFormData(lastUpdatedTimeLabel));

		volumeLabel = getFormToolkit().createLabel(secondRow, null);
		volumeLabel.setLayoutData(createNarrowFormData(volumeIndicatorTextLabel));
		
		Label openPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Op "); //$NON-NLS-1$
		openPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		openPriceIndicatorTextLabel.setLayoutData(createFormData(volumeLabel));

		openPriceLabel = getFormToolkit().createLabel(secondRow, null);
		openPriceLabel.setLayoutData(createNarrowFormData(openPriceIndicatorTextLabel));

		Label highPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Hi "); //$NON-NLS-1$
		highPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		highPriceIndicatorTextLabel.setLayoutData(createFormData(openPriceLabel));

		highPriceLabel = getFormToolkit().createLabel(secondRow, null);
		highPriceLabel.setLayoutData(createNarrowFormData(highPriceIndicatorTextLabel));

		Label lowPriceIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Lo "); //$NON-NLS-1$
		lowPriceIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		lowPriceIndicatorTextLabel.setLayoutData(createFormData(highPriceLabel));

		lowPriceLabel = getFormToolkit().createLabel(secondRow, null);
		lowPriceLabel.setLayoutData(createNarrowFormData(lowPriceIndicatorTextLabel));

		Label tradeValueIndicatorTextLabel = getFormToolkit().createLabel(secondRow, "Trd "); //$NON-NLS-1$
		tradeValueIndicatorTextLabel.setForeground(SYSTEM_COLOR_BLUE);
		tradeValueIndicatorTextLabel.setLayoutData(createFormData(lowPriceLabel));

		tradeValueLabel = getFormToolkit().createLabel(secondRow, null);
		tradeValueLabel.setLayoutData(createNarrowFormData(tradeValueIndicatorTextLabel));

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

	private FormData createFormData(Control leftNeighbor)
	{
		FormData formData = new FormData();
		formData.left = new FormAttachment(leftNeighbor, 12);
		formData.top = new FormAttachment(0);
		return formData;		
	}

	private FormData createNarrowFormData(Control leftNeighbor)
	{
		FormData formData = new FormData();
		formData.left = new FormAttachment(leftNeighbor, 0);
		formData.top = new FormAttachment(0);
		return formData;		
	}

	private FormData createLeftMostControlFormData()
	{
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		return formData;		
	}
	
	public void setAskPriceLabelText(String askPriceLabelText) {
		this.askPriceLabel.setText(askPriceLabelText);
	}
	public void setAskSizeLabelText(String askSizeLabelText) {
		this.askSizeLabel.setText(askSizeLabelText);
	}
	
	public void setBidPriceLabelText(String bidPriceLabelText) {
		this.bidPriceLabel.setText(bidPriceLabelText);
	}
	
	public void setBidSizeLabelText(String bidSizeLabelText) {
		this.bidSizeLabel.setText(bidSizeLabelText);
	}
	
	public void setHighPriceLabelText(String highPriceLabelText) {
		this.highPriceLabel.setText(highPriceLabelText);
	}
	
	public void setInstrumentLabelText(String instrumentLabelText) {
		this.instrumentLabel.setText(instrumentLabelText);
	}
	
	public void setLastPriceChangeLabelText(String lastPriceChangeLabelText) {
		this.lastPriceChangeLabel.setText(lastPriceChangeLabelText);
	}
	public void setLastPriceLabelText(String lastPriceLabelText) {
		this.lastPriceLabel.setText(lastPriceLabelText);
	}

	public void setLastPriceUpDownArrowLabelImage(Image arrowImage) {
		this.lastPriceUpDownArrowLabel.setImage(arrowImage);
	}
	
	public void setLastUpdatedTimeLabelText(String lastUpdatedTimeLabelText) {
		this.lastUpdatedTimeLabel.setText(lastUpdatedTimeLabelText);
	}

	public void setLowPriceLabelText(String lowPriceLabelText) {
		this.lowPriceLabel.setText(lowPriceLabelText);
	}

	public void setOpenPriceLabelText(String openPriceLabelText) {
		this.openPriceLabel.setText(openPriceLabelText);
	}

	public void setTradeValueLabelText(String tradeValueLabelText) {
		this.tradeValueLabel.setText(tradeValueLabelText);
	}

	public void setVolumeLabelText(String volumeLabelText) {
		this.volumeLabel.setText(volumeLabelText);
	}

	public void setExDividendsDateAndAmountItems(String[] dateAmountStrings) {
		StringBuffer dateAmountAsTooltip = new StringBuffer();
		for (String str : dateAmountStrings)
		{
			dateAmountAsTooltip.append(str);			
			dateAmountAsTooltip.append("\n");			 //$NON-NLS-1$
		}					
		this.exDivDateAmountTextLabel.setToolTipText(dateAmountAsTooltip.toString());
		this.exDivDateAmount.setToolTipText(dateAmountAsTooltip.toString());
		this.exDivDateAmount.setItems(dateAmountStrings);
		
	}

}