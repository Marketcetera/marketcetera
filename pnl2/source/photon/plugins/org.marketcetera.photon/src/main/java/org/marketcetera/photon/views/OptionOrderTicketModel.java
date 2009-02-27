package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.Pair;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.ui.databinding.ObservableEventList;
import org.marketcetera.photon.ui.databinding.OptionSpecifierMatcherEditor;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.UniqueList;

/* $License$ */

/**
 * Implementation of the model for an option order ticket.  This subclass
 * of {@link OrderTicketModel} adds four main things.  First, it adds
 * a number of fields for storing and updating underlying stock market data.
 * Second, it maintains lists of valid values for option specifiers, expiration
 * month, expiration year, and strike price.  Third it provides a mechanism for 
 * filtering these specifiers to produce a consistent set based on the state of
 * the UI. And finally this model provides a place to hold option market data for
 * display to the end user.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 *
 */
@ClassVersion("$Id")
public class OptionOrderTicketModel extends OrderTicketModel {

	private final IObservableList expirationMonthList;
	private final IObservableList expirationYearList;
	private final IObservableList strikePriceList;
	private final WritableList optionMarketDataList = new WritableList();
	
	private final WritableValue currentOptionSymbol = new WritableValue("", String.class); //$NON-NLS-1$
	private final WritableValue observableSymbol = new WritableValue("", String.class); //$NON-NLS-1$
	
	private final OptionDateHelper optionDateHelper = new OptionDateHelper();

	private final OptionSpecifierMatcherEditor optionMatcher = new OptionSpecifierMatcherEditor();
	private final TransformedList<OptionContractData, OptionContractData> cachedOptionList = GlazedLists.threadSafeList(new BasicEventList<OptionContractData>());
	private final FilterList<OptionContractData> filteredOptionList = new FilterList<OptionContractData>(cachedOptionList, optionMatcher);
	private boolean updatingOptionInfo = false;

	/**
	 * Create a new model for the option order ticket.  This constructor
	 * also creates the list pipes responsible for updating the expiration month
	 * list, the expiration year list, and the strike price list, based on changes
	 * to the cached option list.
	 * 
	 * @param messageFactory the message factory to use for message creation and augmentation
	 */
	public OptionOrderTicketModel(FIXMessageFactory messageFactory) {
		super(messageFactory);
		
		expirationMonthList = newSelectorList(
				new FunctionList.Function<OptionContractData, Integer>(){
					public Integer evaluate(OptionContractData sourceValue) {
						Integer expirationMonth = sourceValue.getExpirationMonth();
						return expirationMonth;
					}
				},
				new FunctionList.Function<Integer, String>() {
					public String evaluate(Integer sourceValue) {
						String monthAbbreviation = optionDateHelper.getMonthAbbreviation(sourceValue);
						return monthAbbreviation;
					}
				});
		expirationYearList = newSelectorList(
				new FunctionList.Function<OptionContractData, Integer>(){
					public Integer evaluate(OptionContractData sourceValue) {
						return sourceValue.getExpirationYear();
					}
				},
				new FunctionList.Function<Integer, String>() {
					public String evaluate(Integer sourceValue) {
						return ""+sourceValue; //$NON-NLS-1$
					}
				});
		strikePriceList = newSelectorList(
				new FunctionList.Function<OptionContractData, BigDecimal>(){
					public BigDecimal evaluate(OptionContractData sourceValue) {
						BigDecimal strikePrice = sourceValue.getStrikePrice();
						return strikePrice;
					}
				},
				new FunctionList.Function<BigDecimal, String>() {
					public String evaluate(BigDecimal sourceValue) {
						String plainString = sourceValue.toPlainString();
						return plainString;
					}
				});
	}

	/**
	 * Create a new selector list that knows how to transform an {@link OptionContractData}
	 * list into a list suitable for use in the UI.  Specifically these lists
	 * are used to represent the combo choices for certain option specifiers,
	 * expiration year for example.
	 * 
	 * The resulting list will be sorted and uniq'ed
	 * 
	 * @param <T> the type of element in the returned list
	 * @param extractValueFunction the function to extract values from the OptionContractData
	 * @param formatValueFunction the function to format the value into a string
	 * @return an IObservableList with elements representing choices for the UI
	 */
	private <T> IObservableList newSelectorList(
			FunctionList.Function<OptionContractData, T> extractValueFunction,
			FunctionList.Function<T, String> formatValueFunction) {
		return new ObservableEventList(
				new FunctionList<T, String>(
					new UniqueList<T>(
						new SortedList<T>(
							new FunctionList<OptionContractData, T>(
									filteredOptionList, 
									extractValueFunction
							)
						)
					),
					formatValueFunction
				),
				String.class);

	}
		
	/**
	 * Get the list containing specifiers for all the known option series.
	 * @return the list
	 */
	public EventList<OptionContractData> getCachedOptionList() {
		return cachedOptionList;
	}

	/**
	 * Get the list containing all known expiration months.  The values in 
	 * this list may be filtered based on the values of other specifiers,
	 * such as year or strike price.
	 * 
	 * @return the list of expiration months.
	 */
	public IObservableList getExpirationMonthList()
	{
		return expirationMonthList;
	}
	
	/**
	 * Get the list containing all known expiration years.  The values in 
	 * this list may be filtered based on the values of other specifiers,
	 * such as expiration month or strike price.
	 * 
	 * @return the list of expiration years.
	 */
	public IObservableList getExpirationYearList()
	{
		return expirationYearList;
	}
	
	/**
	 * Get the list containing all known strike prices.  The values in 
	 * this list may be filtered based on the values of other specifiers,
	 * such as expiration month or year.
	 * 
	 * @return the list of expiration strike prices.
	 */
	public IObservableList getStrikePriceList() {
		return strikePriceList;
	}

	/**
	 * Get the WritableValue corresponding to the current "full" option symbol
	 * e.g. "IBM+RE".  This value can be set in two main ways.  First if the end
	 * user types in a full option symbol into the Symbol field, this value will
	 * be updated with that symbol.  Alternatively if the user specifies a set of 
	 * criteria for the option order that uniquely identify a particular option, 
	 * the current option symbol will be updated with that symbol.
	 * 
	 * @return the WritableValue representing the currently selected option symbol
	 */
	public WritableValue getCurrentOptionSymbol() {
		return currentOptionSymbol;
	}
	
	/**
	 * Update the filters on option specifiers based on currently selected
	 * option criteria.
	 * 
	 * This method operates in two main modes. If a full option symbol is
	 * specified by the end user, this method will simply use that as the 
	 * sole criterion, clearing all of the rest.
	 * 
	 * If however the user enters any combination of option root, expiration
	 * month, expiration year, and strike price, those criteria will be used as
	 * the filters.
	 * 
	 */
	public void updateOptionInfo() {
			if (!updatingOptionInfo){
				updatingOptionInfo = true;
				try {
					String symbol = null;
					symbol = (String) observableSymbol.getValue();
					if (symbol != null){
						symbol = symbol.trim();
						symbol = symbol.length() == 0 ? null : symbol;
					}
					if (OptionMarketDataUtils.isOptionSymbol(symbol)){
						currentOptionSymbol.setValue(symbol);
						optionMatcher.setOptionSymbol(symbol);
					} else {
						if (orderMessage == null){
							optionMatcher.clearCriteria();
						} else {
							optionMatcher.setOptionRoot(symbol);
							try {
								Pair<Integer, Integer> monthYear = OptionMarketDataUtils.getMaturityMonthYear(orderMessage);
								optionMatcher.setExpirationMonth(monthYear.getFirstMember());
								optionMatcher.setExpirationYear(monthYear.getSecondMember());
							} catch (Exception ex){
								optionMatcher.setExpirationMonth(null);
								optionMatcher.setExpirationYear(null);
							}
							try {
								optionMatcher.setStrikePrice(orderMessage.getDecimal(StrikePrice.FIELD));
							} catch (FieldNotFound fnf){
								optionMatcher.setStrikePrice(null);
							}
							try {
								optionMatcher.setPutOrCall(orderMessage.getInt(PutOrCall.FIELD));
							} catch (FieldNotFound fnf){
								optionMatcher.setPutOrCall(null);
							}
						}
						if (filteredOptionList.size() == 1){
							currentOptionSymbol.setValue(filteredOptionList.get(0).getOptionSymbol().toString());
						} else {
							currentOptionSymbol.setValue(null);
						}
					}
				} finally {
					updatingOptionInfo = false;
				}
			}
	}
	
	@Override
	public boolean isOrderMessageValid() {
		String theSymbol = (String) observableSymbol.getValue();
		String optionSymbol = (String) currentOptionSymbol.getValue();
		
		boolean hasRequiredOrderFields = orderMessage.isSetField(Side.FIELD)
				&& orderMessage.isSetField(OrderQty.FIELD)
				&& orderMessage.isSetField(Price.FIELD)
				&& orderMessage.isSetField(TimeInForce.FIELD);
		boolean validSymbol = theSymbol != null && theSymbol.length() > 0;

		return hasRequiredOrderFields && validSymbol && 
		(
				// either allow a full optionSymbol
				(optionSymbol != null && optionSymbol.length() > 0) ||
				(
						// or allow option specifiers
						orderMessage.isSetField(StrikePrice.FIELD)
						&& orderMessage.isSetField(MaturityMonthYear.FIELD)
						&& orderMessage.isSetField(PutOrCall.FIELD)
				)
		);
	}

	@Override
	public void clearOrderMessage() {
		super.clearOrderMessage();
		this.currentOptionSymbol.setValue(""); //$NON-NLS-1$
		this.observableSymbol.setValue(""); //$NON-NLS-1$
		updateOptionInfo();
	}
	
	@Override
	protected Message createNewOrder() {
		Message aMessage = getMessageFactory().newBasicOrder();
		aMessage.setString(SecurityType.FIELD, SecurityType.OPTION);
		aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
		return aMessage;
	}
	
	/**
	 * Add a new {@link OptionContractData} descriptor to the known list of options.
	 * @param optionContractData the descriptor to add.
	 */
	public void addOptionContractData(OptionContractData optionContractData){
		cachedOptionList.add(optionContractData);
	}
	
	/**
	 * Add all of the {@link OptionContractData} descriptors in the given collection
	 * to the list of known options.
	 * @param optionContractData the collection of descriptors
	 */
	public void addOptionContractData(Collection<OptionContractData> optionContractData){
		cachedOptionList.addAll(optionContractData);
	}

	/**
	 * Remove all the "known" options with the specified option root.
	 * This method is useful when the market data feed returns a new set of 
	 * options for a particular option root.
	 * 
	 * @param optionRoot the root symbol for options to remove
	 */
	public void removeDataForOptionRoot(String optionRoot){
		Iterator<OptionContractData> iter = cachedOptionList.iterator();
		while (iter.hasNext()){
			OptionContractData ocd = iter.next();
			if (optionRoot.equals(ocd.getOptionRoot())){
				iter.remove();
			}
		}
	}

	/**
	 * Remove all "known" options from this order ticket model.
	 */
	public void removeAllCachedOptionData(){
		cachedOptionList.clear();
	}
	
	/**
	 * Complete this message, by updating the option symbol.
	 */
	@Override
	public void completeMessage() throws CoreException {
		super.completeMessage();
		if (currentOptionSymbol.getValue() != null){
			orderMessage.setField(new Symbol((String) currentOptionSymbol.getValue()));
		} else if (observableSymbol.getValue() != null){
			orderMessage.setField(new Symbol((String) observableSymbol.getValue()));
		}
	}

	@Override
	public WritableValue getObservableSymbol(){
		return observableSymbol;
	}

	/**
	 * Get the list of {@link OptionMessageHolder}s that represent
	 * the relevant option market data to this order ticket.
	 * @return the WritableList containing OptionMessageHolders
	 */
	public WritableList getOptionMarketDataList() {
		return optionMarketDataList;
	}
}
