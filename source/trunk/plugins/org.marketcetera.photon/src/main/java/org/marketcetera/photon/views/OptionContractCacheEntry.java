package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.OptionContractData;

/**
 * Cache entry of option contract information for display in the UI.
 */
public class OptionContractCacheEntry {
	private MSymbol underlyingSymbol;

	private List<OptionContractData> optionContracts;

	private List<String> expirationYearsForUI;

	private List<String> expirationMonthsForUI;

	private List<String> strikePricesForUI;

	private String yearPrefix;

	private HashMap<OptionCodeUIValues, OptionContractData> optionCodeToContractMap;

	private OptionDateHelper optionDateHelper = new OptionDateHelper();
	
	private HashMap<MSymbol, OptionCodeUIValues> optionContractSymbolToUIValuesMap;

	public OptionContractCacheEntry(List<OptionContractData> optionContracts) {
		this.optionContracts = optionContracts;
		parseOptionContracts();
	}

	private void logContractSpecs(OptionContractData optionContract) {
		if (PhotonPlugin.getMainConsoleLogger().isDebugEnabled()) {
			PhotonPlugin.getMainConsoleLogger().debug(
					"" + optionContract.getUnderlyingSymbol() + ", "
							+ optionContract.getOptionSymbol() + ", "
							+ optionContract.getStrikePrice() + ", "
							+ optionContract.getExpirationYear() + "-"
							+ optionContract.getExpirationMonth());
		}
	}

	private void parseOptionContracts() {
		optionCodeToContractMap = new HashMap<OptionCodeUIValues, OptionContractData>();
		optionContractSymbolToUIValuesMap = new HashMap<MSymbol, OptionCodeUIValues>();
		expirationYearsForUI = new ArrayList<String>();
		expirationMonthsForUI = new ArrayList<String>();
		strikePricesForUI = new ArrayList<String>();
		HashSet<String> yearsSet = new HashSet<String>();
		// Use ints for the months so they're chronologically sortable.
		HashSet<Integer> monthsSet = new HashSet<Integer>();
		HashSet<String> strikePricesSet = new HashSet<String>();

		for (OptionContractData optionContract : optionContracts) {
			logContractSpecs(optionContract);
			if (underlyingSymbol == null) {
				underlyingSymbol = optionContract.getUnderlyingSymbol();
			}
			if (yearPrefix == null) {
				yearPrefix = optionContract.getExpirationYear().substring(0, 2);
			}

			String yearSuffix = optionContract.getExpirationYear().substring(2,
					4);
			int monthNumber = optionDateHelper.getMonthNumber(optionContract
					.getExpirationMonth());
			monthsSet.add(monthNumber);
			String monthAbbrev = optionDateHelper
					.getMonthAbbreviation(optionContract.getExpirationMonth());

			String strikePrice = optionContract.getStrikePrice()
					.toPlainString();
			OptionCodeUIValues uiKey = new OptionCodeUIValues(yearSuffix, monthAbbrev,
					strikePrice, optionContract.isPut());
			optionCodeToContractMap.put(uiKey, optionContract);
			optionContractSymbolToUIValuesMap.put(optionContract.getOptionSymbol(), uiKey);

			yearsSet.add(yearSuffix);
			strikePricesSet.add(strikePrice);
		}

		// Ensure that the months/years/strikes appear in ascending order when
		// added to a Combo later by sorting them here.

		expirationYearsForUI.addAll(yearsSet);
		Collections.sort(expirationYearsForUI);

		ArrayList<Integer> monthIntsList = new ArrayList<Integer>();
		monthIntsList.addAll(monthsSet);
		Collections.sort(monthIntsList);
		for (int monthNumber : monthIntsList) {
			String monthAbbrev = optionDateHelper
					.getMonthAbbreviation(monthNumber);
			// Do not alter the monthAbbrev here. It must match the one used in
			// the OptionCodeKey above.
			expirationMonthsForUI.add(monthAbbrev);
		}

		strikePricesForUI.addAll(strikePricesSet);
		Collections.sort(strikePricesForUI);
	}

	public OptionCodeUIValues getOptionCodeUIValues(MSymbol optionContractSymbol) {
		return this.optionContractSymbolToUIValuesMap.get(optionContractSymbol);
	}
	
	public OptionContractData getOptionContractData(String uiExpirationYear,
			String uiExpirationMonth, String uiStrikePrice, boolean putWhenTrue) {
		OptionCodeUIValues key = new OptionCodeUIValues(uiExpirationYear,
				uiExpirationMonth, uiStrikePrice, putWhenTrue);
		return optionCodeToContractMap.get(key);
	}

	public List<String> getExpirationMonthsForUI() {
		return expirationMonthsForUI;
	}

	public List<String> getExpirationYearsForUI() {
		return expirationYearsForUI;
	}

	public List<String> getStrikePricesForUI() {
		return strikePricesForUI;
	}

	/**
	 * Hash key for option contract UI values such as "JAN","07".
	 */
	public static class OptionCodeUIValues {
		private String expirationYear;

		private String expirationMonth;

		private String strikePrice;

		private boolean putWhenTrue;

		public boolean isPut() {
			return putWhenTrue;
		}

		public OptionCodeUIValues(String expirationYear, String expirationMonth,
				String strikePrice, boolean putWhenTrue) {
			this.expirationYear = expirationYear;
			this.expirationMonth = expirationMonth;
			this.strikePrice = strikePrice;
			this.putWhenTrue = putWhenTrue;
		}

		public String getExpirationMonth() {
			return expirationMonth;
		}

		public String getExpirationYear() {
			return expirationYear;
		}

		public String getStrikePrice() {
			return strikePrice;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME
					* result
					+ ((expirationMonth == null) ? 0 : expirationMonth
							.hashCode());
			result = PRIME
					* result
					+ ((expirationYear == null) ? 0 : expirationYear.hashCode());
			result = PRIME * result + (putWhenTrue ? 1231 : 1237);
			result = PRIME * result
					+ ((strikePrice == null) ? 0 : strikePrice.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final OptionCodeUIValues other = (OptionCodeUIValues) obj;
			if (expirationMonth == null) {
				if (other.expirationMonth != null)
					return false;
			} else if (!expirationMonth.equals(other.expirationMonth))
				return false;
			if (expirationYear == null) {
				if (other.expirationYear != null)
					return false;
			} else if (!expirationYear.equals(other.expirationYear))
				return false;
			if (putWhenTrue != other.putWhenTrue)
				return false;
			if (strikePrice == null) {
				if (other.strikePrice != null)
					return false;
			} else if (!strikePrice.equals(other.strikePrice))
				return false;
			return true;
		}

	}
}
