package org.marketcetera.photon.model;

import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.editors.ClOrdIDComparator;
import org.marketcetera.photon.editors.LatestMessageFunction;
import org.marketcetera.photon.views.FIXMatcher;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GroupingList;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;

@ClassVersion("$Id$")
public class FIXMessageHistory extends PlatformObject {

	private EventList<MessageHolder> allMessages;
	
	private FilterList<MessageHolder> allFilteredMessages;

	private FilterList<MessageHolder> fillMessages;

	private GroupingList<MessageHolder> orderIDList;

	private GroupingList<MessageHolder> symbolSideList;

	private FilterList<MessageHolder> averagePriceList;

	private FilterList<MessageHolder> latestExecutionReportsList;

	private FilterList<MessageHolder> latestMessageList;
	
	private FilterList<MessageHolder> openOrderList;
	
	private int messageReferenceCounter = 0;

	public FIXMessageHistory() {
		allMessages = new BasicEventList<MessageHolder>();
		allFilteredMessages = new FilterList<MessageHolder>(allMessages);
		fillMessages = new FilterList<MessageHolder>(allFilteredMessages, new FillMatcher());
		orderIDList = new GroupingList<MessageHolder>(allMessages, new ClOrdIDComparator());
		latestExecutionReportsList = new FilterList<MessageHolder>(
			new FunctionList<List<MessageHolder>, MessageHolder>(orderIDList,
				new LatestExecutionReportsFunction()), new NotNullMatcher());
		latestMessageList = new FilterList<MessageHolder>(
				new FunctionList<List<MessageHolder>, MessageHolder>(orderIDList,
					new LatestMessageFunction()), new NotNullMatcher());
		symbolSideList = new GroupingList<MessageHolder>(allFilteredMessages, new SymbolSideComparator());
		averagePriceList = new FilterList<MessageHolder>(
				new FunctionList<List<MessageHolder>, MessageHolder>(symbolSideList,
				new AveragePriceFunction()), new NotNullMatcher());
		openOrderList = new FilterList<MessageHolder>(latestExecutionReportsList, new OpenOrderMatcher());
	}
	
	public FIXMessageHistory(List<MessageHolder> messages){
		this();
		allMessages.addAll(messages);
	}

	public void addIncomingMessage(quickfix.Message fixMessage) {
		allMessages.add(new IncomingMessageHolder(fixMessage, messageReferenceCounter++));
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		allMessages.add(new OutgoingMessageHolder(fixMessage, messageReferenceCounter++));
	}


	public EventList<MessageHolder> getLatestExecutionReportsList() {
		return latestExecutionReportsList;
	}

	public FilterList<MessageHolder> getFillsList() {
		return fillMessages;
	}
	
	public EventList<MessageHolder> getAveragePricesList()
	{
		return averagePriceList;
	}
	
	public int size() {
		return allMessages.size();
	}

	public Message getLatestExecutionReport(String clOrdID) {
		FilterList<MessageHolder> list = new FilterList<MessageHolder>(latestExecutionReportsList,new FIXMatcher<String>(ClOrdID.FIELD, clOrdID));
		if (list.size()>0) {
			return list.get(0).getMessage();
		} else {
			return null;
		}
	}

	public EventList<MessageHolder> getAllMessagesList() {
		return allMessages;
	}

	public Message getLatestMessage(String clOrdID) {
		FilterList<MessageHolder> list = new FilterList<MessageHolder>(latestMessageList,new FIXMatcher<String>(ClOrdID.FIELD, clOrdID));
		if (list.size()>0){
			return list.get(0).getMessage();
		} else {
			return null;
		}
	}

	public void setMatcherEditor(ThreadedMatcherEditor<MessageHolder> matcherEditor) {
		allFilteredMessages.setMatcherEditor(matcherEditor);
	}

	public EventList<MessageHolder> getFilteredMessages() {
		return allFilteredMessages;
	}

	public FilterList<MessageHolder> getOpenOrdersList() {
		return openOrderList;
	}
	
}
