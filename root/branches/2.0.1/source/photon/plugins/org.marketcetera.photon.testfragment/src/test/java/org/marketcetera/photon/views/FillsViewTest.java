package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.core.instruments.MockUnderlyingSymbolSupport;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.messagehistory.TradeReportsHistoryTest;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.UserID;

import com.google.common.collect.ImmutableMap;

import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Urgency;
import quickfix.fix42.ExecutionReport;

public class FillsViewTest
	extends ViewTestBase
{
	public FillsViewTest(String name)
	{
		super(name);
	}
	public void testShowMessage() throws Exception
	{
		TradeReportsHistory hist = new TradeReportsHistory(FIXVersion.FIX_SYSTEM.getMessageFactory(), new MockUnderlyingSymbolSupport());
		FillsView view = (FillsView) getTestView();
		view.setInput(hist);
		hist.addIncomingMessage(OrderManagerTest.createReport(new ExecutionReport(new OrderID("orderid1"),
													new ExecID("execid1"),
													new ExecTransType(ExecTransType.STATUS),
													new ExecType(ExecType.PARTIAL_FILL),
													new OrdStatus(OrdStatus.PARTIALLY_FILLED),
													new Symbol("symbol1"),
													new Side(Side.BUY),
													new LeavesQty(1),
													new CumQty(2),
													new AvgPx(3))));
		delay(1);
		IndexedTableViewer tableViewer = view.getMessagesViewer();
		Table table = tableViewer.getTable();
		assertEquals(0,
					 table.getItemCount());
		ExecutionReport fill = new ExecutionReport(new OrderID("orderid2"),
												   new ExecID("execid2"),
												   new ExecTransType(ExecTransType.STATUS),
												   new ExecType(ExecType.PARTIAL_FILL),
												   new OrdStatus(OrdStatus.PARTIALLY_FILLED),
												   new Symbol("symbol2"),
												   new Side(Side.BUY),
												   new LeavesQty(4),
												   new CumQty(5),
												   new AvgPx(6));
		fill.setField(new LastPx(81));
		fill.setField(new LastQty(91));
		hist.addIncomingMessage(OrderManagerTest.createReport(fill));
		delay(1);
		TableItem item = table.getItem(0);
		ReportHolder returnedMessageHolder = (ReportHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals("orderid2",
					 message.getString(OrderID.FIELD));
	}
	/**
	 * Tests the filtering ability of the view.
	 * 
	 * @throws Exception
	 */
	public void testFilter()
		throws Exception
	{
		doFilterTest();
	}
	@Override
	protected String getViewID()
	{
		return FillsView.ID;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestConditions()
	 */
	@Override
	protected List<FilterTestCondition> getFilterTestConditions()
	{
		List<FilterTestCondition> conditions = new ArrayList<FilterTestCondition>();
		// string match on existing field
		conditions.add(new FilterTestCondition(new FIXStringMatcher(Side.FIELD,
																	"B"),
											   new int[] { 0, 1 }));
		// string no match on existing field
		conditions.add(new FilterTestCondition(new FIXStringMatcher(Symbol.FIELD,
																	"symbol-not-present"),
											   new int[] {}));
		// string match on non-existent field
		conditions.add(new FilterTestCondition(new FIXStringMatcher(Urgency.FIELD,
																	"0"),
											   new int[] {}));
		// regex match on existing field
		conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
																   "[a-z]*bol[1|2]"),
											   new int[] { 0, 1 }));
		// regex match on non-existent field
		conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
																   "[a-z]*bxol[1|2]"),
											   new int[] {}));
		// regex no match in non-existent field
		conditions.add(new FilterTestCondition(new FIXRegexMatcher(Urgency.FIELD,
																   ".*"),
											   new int[] {}));
		return conditions;
	}
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestMessages()
	 */
	@Override
	protected List<Message> getFilterTestMessages()
	{
		List<Message> messages = new ArrayList<Message>();
		ExecutionReport fill1 = new ExecutionReport(new OrderID("orderid1"),
													new ExecID("execid1"),
													new ExecTransType(ExecTransType.STATUS),
													new ExecType(ExecType.PARTIAL_FILL),
													new OrdStatus(OrdStatus.PARTIALLY_FILLED),
													new Symbol("symbol1"),
													new Side(Side.BUY),
													new LeavesQty(4),
													new CumQty(5),
													new AvgPx(6));
		fill1.setField(new LastPx(81));
		fill1.setField(new LastQty(91));
		messages.add(fill1);
		ExecutionReport fill2 = new ExecutionReport(new OrderID("orderid2"),
													new ExecID("execid2"),
													new ExecTransType(ExecTransType.STATUS),
													new ExecType(ExecType.PARTIAL_FILL),
													new OrdStatus(OrdStatus.PARTIALLY_FILLED),
													new Symbol("symbol2"),
													new Side(Side.BUY),
													new LeavesQty(4),
													new CumQty(5),
													new AvgPx(6));
		fill2.setField(new LastPx(81));
		fill2.setField(new LastQty(91));
		messages.add(fill2);
		ExecutionReport fill3 = new ExecutionReport(new OrderID("orderid3"),
													new ExecID("execid3"),
													new ExecTransType(ExecTransType.STATUS),
													new ExecType(ExecType.FILL),
													new OrdStatus(OrdStatus.FILLED),
													new Symbol("symbol3"),
													new Side(Side.SELL),
													new LeavesQty(4),
													new CumQty(5),
													new AvgPx(6));
		fill3.setField(new LastPx(81));
		fill3.setField(new LastQty(91));
		messages.add(fill3);
		return messages;
	}
	
	public void testPartName() {
		FillsView view = (FillsView) getTestView();
		assertThat(view.getPartName(), is("Fills"));
		EnumMap<Grouping, String> filters = new EnumMap<Grouping, String>(Grouping.class);
		filters.put(Grouping.Underlying, "ASDF");
		filters.put(Grouping.Account, null);
		view.setFillsFilter(filters, null);
		assertThat(view.getPartName(), is("Position Fills [Underlying=ASDF,Account=<none>]"));
		filters = new EnumMap<Grouping, String>(Grouping.class);
		filters.put(Grouping.Underlying, "METC");
		filters.put(Grouping.Trader, null);
		view.setFillsFilter(filters, null);
		assertThat(view.getPartName(), is("Position Fills [Underlying=METC,Trader=<none>]"));
		filters = new EnumMap<Grouping, String>(Grouping.class);
        filters.put(Grouping.Underlying, "METC");
        filters.put(Grouping.Trader, null);
        filters.put(Grouping.Account, "bogus");
        view.setFillsFilter(filters, null);
        assertThat(view.getPartName(), is("Position Fills [Underlying=METC,Account=bogus,Trader=<none>]"));
        view.setFillsFilter(filters, new Equity("METC"));
        assertThat(view.getPartName(), is("Position Fills [Instrument=METC,Account=bogus,Trader=<none>]"));
        view.setFillsFilter(filters, new Option("IBM", "200910", BigDecimal.ONE, OptionType.Call));
        assertThat(view.getPartName(), is("Position Fills [Instrument=Oct 09 IBM Call 1.00,Account=bogus,Trader=<none>]"));
		view.setFillsFilter(null, null);
		assertThat(view.getPartName(), is("Fills"));
	}
	
	public void testSetFillsFilter() throws Exception {
		TradeReportsHistory hist = new TradeReportsHistory(FIXVersion.FIX_SYSTEM
				.getMessageFactory(), new MockUnderlyingSymbolSupport());
		FillsView view = (FillsView) getTestView();
		view.setInput(hist);
		// add some data
		String[] symbols = new String[] {"METC", "JAVA"};
		String[] accounts = new String[] {"A1", "A2", null};
		String[] traderIds = new String[] {"1", "2", null};
		int count = 0;
		for (String symbol : symbols) {
			for (String account : accounts) {
				for (String traderId : traderIds) {
					count++;
					hist.addIncomingMessage(createReport(symbol, account, traderId));
				}
			}
		}
		assertThat(view.getInput().size(), is(count));
		// test all the permutations
		Map<Grouping, String[]> options = new EnumMap<Grouping, String[]>(Grouping.class);
		Map<Grouping, String> currentTest;
		options.put(Grouping.Underlying, symbols);
		options.put(Grouping.Account, accounts);
		options.put(Grouping.Trader, traderIds);
        for (Grouping grouping : Grouping.values()) {
            // test one grouping
            for (String value : options.get(grouping)) {
                currentTest = new EnumMap<Grouping, String>(Grouping.class);
                currentTest.put(grouping, value);
                validate(view, currentTest);
            }
            for (Grouping grouping1 : Grouping.values()) {
                if (grouping1 == grouping) {
                    continue;
                }
                // test two groupings
                for (String value : options.get(grouping)) {
                    for (String value1 : options.get(grouping1)) {
                        currentTest = new EnumMap<Grouping, String>(
                                Grouping.class);
                        currentTest.put(grouping, value);
                        currentTest.put(grouping1, value1);
                        validate(view, currentTest);
                    }
                }
                for (Grouping grouping2 : Grouping.values()) {
                    if (grouping2 == grouping || grouping2 == grouping1) {
                        continue;
                    }
                    // test three groupings
                    for (String value : options.get(grouping)) {
                        for (String value1 : options.get(grouping1)) {
                            for (String value2 : options.get(grouping2)) {
                                currentTest = new EnumMap<Grouping, String>(
                                        Grouping.class);
                                currentTest.put(grouping, value);
                                currentTest.put(grouping1, value1);
                                currentTest.put(grouping2, value2);
                                validate(view, currentTest);
                            }
                        }
                    }
                }
            }
		}
		view.setFillsFilter(null, null);
		assertThat(view.getInput().size(), is(count));
	}
	
    public void testSetFillsFilterWithInstruments() throws Exception {
        TradeReportsHistory hist = new TradeReportsHistory(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), new MockUnderlyingSymbolSupport());
        FillsView view = (FillsView) getTestView();
        view.setInput(hist);
        // add some data
        Instrument[] instruments = new Instrument[] {
                new Equity("METC"),
                new Option("METC", "20090101", new BigDecimal("1"),
                        OptionType.Put),
                new Option("JAVA", "20090101", new BigDecimal("1"),
                        OptionType.Put) };
        String[] accounts = new String[] { "A1", "A2", null };
        String[] traderIds = new String[] { "1", "2", null };
        int count = 0;
        for (Instrument instrument : instruments) {
            for (String account : accounts) {
                for (String traderId : traderIds) {
                    count++;
                    hist.addIncomingMessage(createReport(instrument, account,
                            traderId));
                }
            }
        }
        assertThat(view.getInput().size(), is(count));
        /*
         * Test all the permutations - when instrument is used, account and
         * trader will always be set and underlying is ignored.
         */
        Map<Grouping, String[]> options = new EnumMap<Grouping, String[]>(
                Grouping.class);
        Map<Grouping, String> currentTest;
        options.put(Grouping.Account, accounts);
        options.put(Grouping.Trader, traderIds);
        for (Instrument instrument : instruments) {
            Grouping grouping = Grouping.Account;
            Grouping grouping1 = Grouping.Trader;
            for (String value : options.get(grouping)) {
                for (String value1 : options.get(grouping1)) {
                    currentTest = new EnumMap<Grouping, String>(Grouping.class);
                    currentTest.put(grouping, value);
                    currentTest.put(grouping1, value1);
                    validate(view, instrument, currentTest);
                }
            }
        }
        view.setFillsFilter(null, null);
        assertThat(view.getInput().size(), is(count));
    }
    
    public void testOPRAStyleOption() throws Exception {
        UnderlyingSymbolSupport mockUnderlyingSymbolSupport = mock(UnderlyingSymbolSupport.class);
        TradeReportsHistory hist = new TradeReportsHistory(
                FIXVersion.FIX_SYSTEM.getMessageFactory(), mockUnderlyingSymbolSupport);
        FillsView view = (FillsView) getTestView();
        view.setInput(hist);
        // add some data
        Equity metcEquity = new Equity("METC");
        Option metcOption1 = new Option("MXX", "20090101", new BigDecimal("1"),
                OptionType.Put);
        Option metcOption2 = new Option("MXY", "20090101", new BigDecimal("1"),
                OptionType.Put);
        when(mockUnderlyingSymbolSupport.getUnderlying(metcOption1)).thenReturn("METC");
        when(mockUnderlyingSymbolSupport.getUnderlying(metcOption2)).thenReturn("METC");
        when(mockUnderlyingSymbolSupport.getUnderlying(metcEquity)).thenReturn("METC");
        Instrument[] instruments = new Instrument[] {
                metcEquity,
                metcOption1,
                metcOption2,
                new Option("JAVA", "20090101", new BigDecimal("1"),
                        OptionType.Put) };
        int count = 0;
        for (Instrument instrument : instruments) {
            count++;
            hist.addIncomingMessage(createReport(instrument, "A", "1"));
        }
        assertThat(view.getInput().size(), is(count));
        validate(view, ImmutableMap.of(Grouping.Underlying, "METC"));
        assertThat(view.getInput().size(), is(3));
        view.setFillsFilter(null, null);
        assertThat(view.getInput().size(), is(count));
    }
	
	private void validate(FillsView view, Map<Grouping, String> currentTest) {
		view.setFillsFilter(currentTest, null);
		for (ReportHolder holder : view.getInput()) {
			validate(holder, currentTest);
		}
	}
    
    private void validate(FillsView view, Instrument instrument, Map<Grouping, String> currentTest) {
        view.setFillsFilter(currentTest, instrument);
        for (ReportHolder holder : view.getInput()) {
            validate(holder, instrument, currentTest);
        }
    }
	
	private void validate(ReportHolder holder,
			final Map<Grouping, String> currentTest) {
		org.marketcetera.trade.ExecutionReport report = (org.marketcetera.trade.ExecutionReport) holder
				.getReport();
		for (Map.Entry<Grouping, String> entry : currentTest.entrySet()) {
			switch (entry.getKey()) {
			case Underlying:
				assertThat(holder.getUnderlying(), is(entry.getValue()));
				break;
			case Account:
				assertThat(report.getAccount(), is(entry.getValue()));
				break;
			case Trader:
				UserID viewerID = report.getViewerID();
				String viewerString = viewerID == null ? null : viewerID.toString();
				assertThat(viewerString, is(entry.getValue()));
				break;
			default:
				fail("Unexpected grouping type " + entry.getKey());
			}
		}
	}
    
    private void validate(ReportHolder holder, Instrument instrument,
            final Map<Grouping, String> currentTest) {
        org.marketcetera.trade.ExecutionReport report = (org.marketcetera.trade.ExecutionReport) holder
                .getReport();
        assertThat(report.getInstrument(), is(instrument));
        validate(holder, currentTest);
    }
	
	private ReportBase createReport(String symbol, String account, String traderId) throws Exception {
		ExecutionReport report = new ExecutionReport();
		report.set(new Symbol(symbol));
		return finishReport(account, traderId, report);
	}
	
    private ReportBase finishReport(String account, String traderId,
            ExecutionReport report) throws MessageCreationException {
        if (account != null) report.set(new Account(account));
		report.set(new OrdStatus(OrdStatus.FILLED));
		report.set(new LastShares(BigDecimal.TEN));
		return TradeReportsHistoryTest.assignReportID(Factory.getInstance().createExecutionReport(
				report, null, Originator.Server, null,
				traderId == null ? null : new UserID(Long.valueOf(traderId))));
    }
    
    private ReportBase createReport(Instrument instrument, String account, String traderId) throws Exception {
        ExecutionReport report = new ExecutionReport();
        InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument, FIXVersion.FIX42.toString(), report);
        return finishReport(account, traderId, report);
    }
}
