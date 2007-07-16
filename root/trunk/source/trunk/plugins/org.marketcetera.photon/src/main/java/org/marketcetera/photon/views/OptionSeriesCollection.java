package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.OptionContractData;

/**
 * Cache entry of option contract information for display in the UI.
 */
public class OptionSeriesCollection {
	private MSymbol underlyingSymbol;

	private List<String> expirationYearsForUI;

	private List<String> expirationMonthsForUI;

	private List<String> strikePricesForUI;

	private String yearPrefix;

	private OptionDateHelper optionDateHelper = new OptionDateHelper();
	
	private HashMap<MSymbol, OptionContractData> optionSymbolToInfoMap;
	
	private TreeMap<OptionContractData, OptionContractData> uiInfoSet;

	public OptionSeriesCollection(List<OptionContractData> optionContracts) {
		parseOptionContracts(optionContracts);
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

	private void parseOptionContracts(List<OptionContractData> optionContracts) {
		optionSymbolToInfoMap = new HashMap<MSymbol, OptionContractData>();
		uiInfoSet = new TreeMap<OptionContractData, OptionContractData>(new OptionContractData.UIOnlyComparator());
		expirationYearsForUI = new ArrayList<String>();
		expirationMonthsForUI = new ArrayList<String>();
		strikePricesForUI = new ArrayList<String>();
		HashSet<String> yearsSet = new HashSet<String>();
		// Use ints for the months so they're chronologically sortable.
		HashSet<Integer> monthsSet = new HashSet<Integer>();
		HashSet<String> strikePricesSet = new HashSet<String>();

		for (OptionContractData optionContract : optionContracts) {
			yearsSet.add(optionContract.getExpirationYearUIString());
			monthsSet.add(optionContract.getExpirationMonth());
			strikePricesSet.add(optionContract.getStrikePriceUIString());
			optionSymbolToInfoMap.put(optionContract.getOptionSymbol(), optionContract);
			uiInfoSet.put(optionContract, optionContract);
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

	public OptionContractData getOptionInfoForSymbol(MSymbol optionContractSymbol) {
		return optionSymbolToInfoMap.get(optionContractSymbol);
	}
	
	public OptionContractData getOptionContractData(String optionRoot, String uiExpirationYear,
			String uiExpirationMonth, String uiStrikePrice, Integer putOrCall) {
		OptionContractData key = new OptionContractData(optionRoot, uiExpirationYear,
				uiExpirationMonth, uiStrikePrice, putOrCall);
		return uiInfoSet.get(key);
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
}
