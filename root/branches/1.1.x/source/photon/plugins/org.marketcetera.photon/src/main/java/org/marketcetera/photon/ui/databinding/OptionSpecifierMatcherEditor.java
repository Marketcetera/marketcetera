package org.marketcetera.photon.ui.databinding;

import java.math.BigDecimal;

import org.marketcetera.photon.marketdata.OptionContractData;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;

/**
 * This {@link MatcherEditor} knows how to generate two types of matchers.
 * First if optionSymbol has a value, it will match solely on that value.
 * If optionSybol is null, it will attempt to match on all of the other criteria:
 * month, year, strike price, option root, and put-or-call.
 * 
 * @author gmiller
 *
 */
public class OptionSpecifierMatcherEditor extends AbstractMatcherEditor<OptionContractData>{

	private Integer month;
	private Integer year;
	private BigDecimal strikePrice;
	private String optionRoot;
	private Integer putOrCall;
	private String optionSymbol;

	private final class CriteriaMatcherImplementation implements
			Matcher<OptionContractData> {

		private final Integer month;
		private final Integer year;
		private final BigDecimal strikePrice;
		private final String optionRoot;
		private final Integer putOrCall;
		
		public CriteriaMatcherImplementation(String optionRoot, Integer month,
				Integer year, BigDecimal strikePrice,
				Integer putOrCall) {
			this.month = month;
			this.year = year;
			this.strikePrice = strikePrice;
			this.optionRoot = optionRoot;
			this.putOrCall = putOrCall;
		}

		public boolean matches(OptionContractData contractData) {
			boolean matches = true;
			if (month != null){
				matches &= month.equals(contractData.getExpirationMonth());
			}
			if (year != null){
				matches &= year.equals(contractData.getExpirationYear());
			}
			if (strikePrice != null){
				matches &= strikePrice.equals(contractData.getStrikePrice());
			}
			if (optionRoot != null){
				matches &= optionRoot.equals(contractData.getOptionRoot());
			}
			if (putOrCall != null){
				matches &= putOrCall.equals(contractData.getPutOrCall());
			}
			return matches;
		}
	}

	private final class SymbolMatcherImplementation implements
		Matcher<OptionContractData> 
	{
		private final String optionSymbol;
		public SymbolMatcherImplementation(String optionSymbol) {
			this.optionSymbol = optionSymbol;
		}
		
		public boolean matches(OptionContractData contractData){
			return contractData.getOptionSymbol().toString().equals(optionSymbol);
		}
	}
	
	public void setExpirationMonth(Integer month) {
		this.optionSymbol = null;
		this.month = month;
		update();
	}

	public void setExpirationYear(Integer year) {
		this.optionSymbol = null;
		this.year = year;
		update();
	}

	public void setStrikePrice(BigDecimal strikePrice) {
		this.optionSymbol = null;
		this.strikePrice = strikePrice;
		update();
	}

	public void setOptionRoot(String optionRoot) {
		this.optionSymbol = null;
		this.optionRoot = optionRoot;
		update();
	}

	public void setPutOrCall(Integer putOrCall) {
		this.optionSymbol = null;
		this.putOrCall = putOrCall;
		update();
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
		update();
	}
	
	public void clearCriteria(){
		this.month = null;
		this.year = null;
		this.strikePrice = null;
		this.optionRoot = null;
		this.putOrCall = null;
		this.optionSymbol = null;
		update();
	}
	
	public void update(){
		Matcher<OptionContractData> matcherImplementation;
		if (optionSymbol == null){
			matcherImplementation = new CriteriaMatcherImplementation(optionRoot, month, year, strikePrice, putOrCall);
		} else {
			matcherImplementation = new SymbolMatcherImplementation(optionSymbol);
		}
		fireChanged(matcherImplementation);
	}
}
