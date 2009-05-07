package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.messagehistory.TradeReportsHistoryTest;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.UserID;

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
		TradeReportsHistory hist = new TradeReportsHistory(FIXVersion.FIX_SYSTEM.getMessageFactory());
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
		filters.put(Grouping.Symbol, "ASDF");
		filters.put(Grouping.Account, null);
		view.setFillsFilter(filters);
		assertThat(view.getPartName(), is("Position Fills [Symbol=ASDF,Account=<none>]"));
		filters = new EnumMap<Grouping, String>(Grouping.class);
		filters.put(Grouping.Symbol, "METC");
		filters.put(Grouping.Trader, null);
		view.setFillsFilter(filters);
		assertThat(view.getPartName(), is("Position Fills [Symbol=METC,Trader=<none>]"));
		view.setFillsFilter(null);
	}
	
	public void testSetFillsFilter() throws Exception {
		TradeReportsHistory hist = new TradeReportsHistory(FIXVersion.FIX_SYSTEM
				.getMessageFactory());
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
		options.put(Grouping.Symbol, symbols);
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
				// test two groupings
				for (String value : options.get(grouping)) {
					for (String value1 : options.get(grouping1)) {
						currentTest = new EnumMap<Grouping, String>(Grouping.class);
						currentTest.put(grouping, value);
						currentTest.put(grouping1, value1);
						validate(view, currentTest);
					}
				}
				for (Grouping grouping2 : Grouping.values()) {
					// test three groupings
					for (String value : options.get(grouping)) {
						for (String value1 : options.get(grouping1)) {
							for (String value2 : options.get(grouping2)) {
								currentTest = new EnumMap<Grouping, String>(Grouping.class);
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
		view.setFillsFilter(null);
		assertThat(view.getInput().size(), is(count));
	}
	
	private void validate(FillsView view, Map<Grouping, String> currentTest) {
		view.setFillsFilter(currentTest);
		for (ReportHolder holder : view.getInput()) {
			validate(holder, currentTest);
		}
	}
	
	private void validate(ReportHolder holder,
			final Map<Grouping, String> currentTest) {
		org.marketcetera.trade.ExecutionReport report = (org.marketcetera.trade.ExecutionReport) holder
				.getReport();
		for (Map.Entry<Grouping, String> entry : currentTest.entrySet()) {
			switch (entry.getKey()) {
			case Symbol:
				assertThat(report.getSymbol().toString(), is(entry.getValue()));
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
	
	private ReportBase createReport(String symbol, String account, String traderId) throws Exception {
		ExecutionReport report = new ExecutionReport();
		report.set(new Symbol(symbol));
		if (account != null) report.set(new Account(account));
		report.set(new OrdStatus(OrdStatus.FILLED));
		report.set(new LastShares(BigDecimal.TEN));
		return TradeReportsHistoryTest.assignReportID(Factory.getInstance().createExecutionReport(
				report, null, Originator.Server, null,
				traderId == null ? null : new UserID(Long.valueOf(traderId))));
	}
}
