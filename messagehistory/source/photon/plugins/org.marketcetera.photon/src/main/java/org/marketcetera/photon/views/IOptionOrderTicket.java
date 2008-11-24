package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public interface IOptionOrderTicket extends IOrderTicket {

	/**
	 * @return symbol for the specific option contract (e.g. "MSQ+GE")
	 */
	Text getOptionSymbolText();

	Combo getExpireMonthCombo();

	Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();
	
	Combo getStrikePriceCombo();

	Combo getExpireYearCombo();

	TableViewer getOptionMarketDataTableViewer();
	
	Composite getUnderlyingMarketDataComposite();

	Label getUnderlyingSymbolLabel();

	Label getUnderlyingLastPriceLabel();

	Label getUnderlyingLastPriceUpDownArrowLabel();

	Label getUnderlyingLastPriceChangeLabel();

	Label getUnderlyingBidPriceLabel();

	Label getUnderlyingOfferPriceLabel();

	Label getUnderlyingBidSizeLabel();

	Label getUnderlyingOfferSizeLabel();
	
	List getExDivDateAmountList();

	Label getUnderlyingLastUpdatedTimeLabel();

	Label getUnderlyingVolumeLabel();

	Label getUnderlyingOpenPriceLabel();

	Label getUnderlyingHighPriceLabel();

	Label getUnderlyingLowPriceLabel();

	Label getUnderlyingTradedValueLabel();


}
