package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.UnderlyingSymbolInfo.UnderlyingSymbolDataFields;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXValueExtractor;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Composite containing one or more underlying symbols info.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class UnderlyingSymbolInfoComposite
    extends Composite
    implements Messages
{
	private FormToolkit formToolkit;

	private Composite underlyingSymbolsContainer;

	private HashMap<String, UnderlyingSymbolInfo> underlyingSymbolInfoMap;

	private DataDictionary dictionary;

	private FIXValueExtractor extractor;

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss"); //$NON-NLS-1$

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd"); //$NON-NLS-1$
	
	public UnderlyingSymbolInfoComposite(Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(createBasicGridLayout(1));
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		createControl(this);
		initializeFixValueExtractor();
	}

	private void initializeFixValueExtractor() {
		dictionary = FIXDataDictionaryManager.getFIXDataDictionary(
				FIXVersion.FIX44).getDictionary();
		FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(
				dictionary.getVersion()).getMessageFactory();
		extractor = new FIXValueExtractor(dictionary, messageFactory);
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

	private void createUnderlyingSymbolsContainerComposite(Composite parent) {
		underlyingSymbolsContainer = getFormToolkit().createComposite(
				parent, SWT.NONE);
		underlyingSymbolsContainer
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		underlyingSymbolsContainer.setLayout(createBasicGridLayout(1));
	}

	private Composite createUnderlyingSymbolComposite(Composite parent) {
		Composite underlyingSymbolComposite = getFormToolkit().createComposite(
				parent, SWT.NONE);
		underlyingSymbolComposite.setLayout(createBasicGridLayout(1));
		underlyingSymbolComposite
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		return underlyingSymbolComposite;
	}

	protected void addUnderlyingSymbolInfo(String underlyingSymbol) {
		if (underlyingSymbolInfoMap.get(underlyingSymbol) != null) {
			removeUnderlyingSymbol();
		}
		if (underlyingSymbolInfoMap.size() > 0) {
			Label separator = new Label(underlyingSymbolsContainer,
					SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			gridData.widthHint = 400;
			separator.setLayoutData(gridData);
		}
		Composite underlyingSymbolComposite = createUnderlyingSymbolComposite(underlyingSymbolsContainer);
		underlyingSymbolInfoMap.put(underlyingSymbol, new UnderlyingSymbolInfo(
				underlyingSymbolComposite));
	}

	private GridData createTopAlignedHorizontallySpannedGridData() {
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
		// formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		return formGridData;
	}

	private GridLayout createBasicGridLayout(int numColumns) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		return gridLayout;
	}
	
	private void disposeUnderlyingInfo() {
		if (underlyingSymbolsContainer == null
				|| underlyingSymbolsContainer.isDisposed()) {
			return;
		}
		Control[] children = underlyingSymbolsContainer.getChildren();
		for (Control child : children) {
			if(child != null && ! child.isDisposed()) {
				try {
					child.dispose();
				} catch (Exception anyException) {
					PhotonPlugin.getMainConsoleLogger().info(FAILED_TO_DISPOSE_MARKET_DATA_WIDGET.getText(),
					                                         anyException);
				}
			}
		}
	}

	protected boolean matchUnderlyingSymbol(Message quote) {
		String quoteSymbol = getSymbol(quote);
		UnderlyingSymbolInfo symbolInfo = underlyingSymbolInfoMap
				.get(quoteSymbol);
		return (symbolInfo != null);
	}

	protected void updateQuote(Message quote) {
		String quoteSymbol = getSymbol(quote);
		UnderlyingSymbolInfo symbolInfo = underlyingSymbolInfoMap
				.get(quoteSymbol);
		if (symbolInfo != null) {
			symbolInfo.setInstrumentLabelText(extractStockValue(
					UnderlyingSymbolDataFields.SYMBOL, quote).toString());
			symbolInfo.setLastPriceLabelText(extractStockValue(
					UnderlyingSymbolDataFields.LASTPX, quote));
			symbolInfo.setLastPriceChangeLabelText((String) extractStockValue(
					UnderlyingSymbolDataFields.LASTPX, quote));

			symbolInfo
					.setAskPriceLabelText(extractStockValue(
							UnderlyingSymbolInfo.UnderlyingSymbolDataFields.ASK,
							quote));
			symbolInfo.setAskSizeLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.ASKSZ,
					quote));

			symbolInfo
					.setBidPriceLabelText(extractStockValue(
							UnderlyingSymbolInfo.UnderlyingSymbolDataFields.BID,
							quote));
			symbolInfo.setBidSizeLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.BIDSZ,
					quote));

			// cl todo:retrieve dateAmountStrings
			// symbolInfo.setExDividendsDateAndAmountItems(dateAmountStrings);

			symbolInfo.setLastUpdatedTimeLabelText(extractStockValue(
					UnderlyingSymbolDataFields.LASTUPDATEDTIME, quote));
			symbolInfo.setOpenPriceLabelText(extractStockValue(
					UnderlyingSymbolDataFields.OPENPX, quote));
			symbolInfo.setOpenPriceLabelText(extractStockValue(
					UnderlyingSymbolDataFields.OPENPX, quote));
			symbolInfo.setHighPriceLabelText(extractStockValue(
					UnderlyingSymbolDataFields.HI, quote));
			symbolInfo.setLowPriceLabelText(extractStockValue(
					UnderlyingSymbolDataFields.LOW, quote));
			symbolInfo.setTradeValueLabelText(extractStockValue(
					UnderlyingSymbolDataFields.TRADEVOL, quote));
			underlyingSymbolsContainer.pack(true);
			underlyingSymbolsContainer.layout(true, true);
		}
	}

	public void onQuote(final Message aQuote) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			updateQuote(aQuote);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					updateQuote(aQuote);
				}
			});
		}
	}

	// cl todo:refactor (existed in both this class and OptionMarketDataView)
	private static String getSymbol(Message message) {
		try {
			return message.getString(Symbol.FIELD).trim();
		} catch (FieldNotFound e) {
			return null;
		}
	}

	// cl todo:clean up this - fieldID should be encapsulate better, refactor
	// common methods from EnumTableFormat into common util class
	private String convertExtractedValue(Object objValue, Integer fieldID) {
		String value = ""; //$NON-NLS-1$
		if (objValue != null && fieldID != null) {
			FieldType fieldType = dictionary.getFieldTypeEnum(fieldID);
			if (fieldType.equals(FieldType.UtcTimeOnly)
					|| fieldType.equals(FieldType.UtcTimeStamp)) {
				value = TIME_FORMAT.format((Date) objValue);
			} else if (fieldType.equals(FieldType.UtcDateOnly)
					|| fieldType.equals(FieldType.UtcDate)) {
				value = DATE_FORMAT.format((Date) objValue);
			} else if (objValue instanceof BigDecimal) {
				value = ((BigDecimal) objValue).toPlainString();
			} else {
				value = objValue.toString();
			}
		}
		return value;
	}

	public String extractStockValue(Enum<?> fieldEnum, Object element) {
		Object value = null;
		Integer fieldID = null;
		if (fieldEnum instanceof IFieldIdentifier) {
			IFieldIdentifier fieldIdentifier = ((IFieldIdentifier) fieldEnum);

			fieldID = fieldIdentifier.getFieldID();
			Integer groupID = fieldIdentifier.getGroupID();
			Integer groupDiscriminatorID = fieldIdentifier
					.getGroupDiscriminatorID();
			Object groupDiscriminatorValue = fieldIdentifier
					.getGroupDiscriminatorValue();

			FieldMap fieldMap = (FieldMap) element;
			value = extractor.extractValue(fieldMap, fieldID, groupID,
					groupDiscriminatorID, groupDiscriminatorValue, true);
		}
		return convertExtractedValue(value, fieldID);
	}

	protected boolean hasSymbol(final MSymbol symbol) {
		return (underlyingSymbolInfoMap.get(symbol.getFullSymbol()) != null);
	}

	protected boolean hasUnderlyingSymbolInfo() {
		return (underlyingSymbolInfoMap != null && underlyingSymbolInfoMap
				.size() > 0);
	}

	protected void removeUnderlyingSymbol() {
		underlyingSymbolInfoMap.clear();
		disposeUnderlyingInfo();
	}

	protected void createControl(Composite parent) {
		underlyingSymbolInfoMap = new HashMap<String, UnderlyingSymbolInfo>();
		createUnderlyingSymbolsContainerComposite(parent);
	}

	@Override
	public void dispose() {
		removeUnderlyingSymbol();
		super.dispose();
	}

	public HashMap<String, UnderlyingSymbolInfo> getUnderlyingSymbolInfoMap() {
		return underlyingSymbolInfoMap;
	}

	public void setUnderlyingSymbolInfoMap(
			HashMap<String, UnderlyingSymbolInfo> underlyingSymbolInfoMap) {
		this.underlyingSymbolInfoMap = underlyingSymbolInfoMap;
	}
	
}
