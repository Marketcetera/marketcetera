package org.marketcetera.photon.views;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.ui.databinding.OptionSpecifierMatcherEditor;
import org.marketcetera.trade.MSymbol;

import quickfix.field.PutOrCall;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

public class OptionSpecifierMatcherTest extends TestCase {

	public void testOptionSpecifierMatcher() throws Exception {
		OptionSpecifierMatcherEditor matcherEditor = new OptionSpecifierMatcherEditor();
		EventList<OptionContractData> list = new BasicEventList<OptionContractData>();
		FilterList<OptionContractData> filteredList = new FilterList<OptionContractData>(list, matcherEditor);
		OptionContractData contractData = new OptionContractData(
				new MSymbol("MSFT"),
				new MSymbol("MSQ+RE"),
				2008,
				1,
				BigDecimal.TEN,
				PutOrCall.PUT
				);
		
		list.add(contractData);

		OptionContractData contractData2 = new OptionContractData(
				new MSymbol("MSFT"),
				new MSymbol("MSQ+RF"),
				2008,
				1,
				BigDecimal.ONE,
				PutOrCall.PUT
				);
		
		list.add(contractData2);

		OptionContractData contractData3 = new OptionContractData(
				new MSymbol("MSFT"),
				new MSymbol("MSF+FD"),
				2008,
				1,
				BigDecimal.TEN,
				PutOrCall.PUT
				);
		
		list.add(contractData3);

		
		matcherEditor.setExpirationMonth(1);
		matcherEditor.setExpirationYear(2008);
		matcherEditor.setStrikePrice(BigDecimal.TEN);
		matcherEditor.setOptionRoot("MSQ");
		matcherEditor.setPutOrCall(PutOrCall.PUT);
		
		assertEquals(1, filteredList.size());
		OptionContractData returnedContractData = filteredList.get(0);
		assertEquals((Integer)1, returnedContractData.getExpirationMonth());
		assertEquals((Integer)2008, returnedContractData.getExpirationYear());
		assertEquals(new BigDecimal(10), returnedContractData.getStrikePrice());
		assertEquals("MSQ", returnedContractData.getOptionRoot().toString());
		assertEquals(PutOrCall.PUT, returnedContractData.getPutOrCall());
		
		matcherEditor.setStrikePrice(null);
		assertEquals(2, filteredList.size());
		returnedContractData = filteredList.get(1);
		assertEquals((Integer)1, returnedContractData.getExpirationMonth());
		assertEquals((Integer)2008, returnedContractData.getExpirationYear());
		assertEquals(BigDecimal.ONE, returnedContractData.getStrikePrice());
		assertEquals("MSQ", returnedContractData.getOptionRoot().toString());
		assertEquals(PutOrCall.PUT, returnedContractData.getPutOrCall());
		
	}
}
