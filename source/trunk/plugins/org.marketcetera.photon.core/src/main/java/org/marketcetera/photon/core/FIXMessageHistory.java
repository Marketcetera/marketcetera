package org.marketcetera.photon.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
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
	
	private Map<String, MessageHolder> orderMap;
	
	private int messageReferenceCounter = 0;

	public FIXMessageHistory(FIXMessageFactory messageFactory) {
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
				new AveragePriceFunction(messageFactory)), new NotNullMatcher());
		openOrderList = new FilterList<MessageHolder>(latestExecutionReportsList, new OpenOrderMatcher());
		
		orderMap = new HashMap<String, MessageHolder>();
	}
	
	public FIXMessageHistory(FIXMessageFactory messageFactory, List<MessageHolder> messages){
		allMessages.addAll(messages);
	}

	public void addIncomingMessage(quickfix.Message fixMessage) {
		allMessages.add(new IncomingMessageHolder(fixMessage, messageReferenceCounter++));
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		OutgoingMessageHolder messageHolder = new OutgoingMessageHolder(fixMessage, messageReferenceCounter++);
		if (FIXMessageUtil.isOrderSingle(fixMessage)){
			try {
				orderMap.put(fixMessage.getString(ClOrdID.FIELD), messageHolder);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		allMessages.add(messageHolder);
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

	// TODO: UUUUUgly
	public Message getOpenOrder(String clOrdID) {
		Message returnMessage = null;
		for (MessageHolder holder : allMessages) {
			try {
				Message aMessage = holder.getMessage();
				if (aMessage.getHeader().getString(MsgType.FIELD).equals(MsgType.ORDER_SINGLE) &&
						clOrdID.equals(aMessage.getString(ClOrdID.FIELD)))
				{
					returnMessage  = aMessage;
				}
			} catch (FieldNotFound e) {
			}
		}
		return returnMessage;
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
	
	public MessageHolder getOrder(String clOrdID){
		return orderMap.get(clOrdID);
	}
	
}
