package org.marketcetera.photon.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.OrderID;
import quickfix.field.SendingTime;
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
	
	private long messageReferenceCounter = 0;

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
		// todo: clean this up after debugging finished
		if(fixMessage.getHeader().isSetField(SendingTime.FIELD)) {
			long sendingTime =0;
			try {
				sendingTime = fixMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD).getTime();
			} catch (FieldNotFound e) {
			}
			long systemTime = System.currentTimeMillis();
			double diff = (sendingTime-systemTime)/1000.0;
			if(Math.abs(diff) > 1) {
				LoggerAdapter.info(Thread.currentThread().getName() + ": sendingTime v systemTime: "+diff, this);
			}
		}
		try {
			allMessages.getReadWriteLock().writeLock().lock();
			allMessages.add(new IncomingMessageHolder(fixMessage, messageReferenceCounter++));
		} finally {
			allMessages.getReadWriteLock().writeLock().unlock();
		}
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		OutgoingMessageHolder messageHolder = new OutgoingMessageHolder(fixMessage, messageReferenceCounter++);
		if (FIXMessageUtil.isOrderSingle(fixMessage)){
			try {
				synchronized (orderMap){
					orderMap.put(fixMessage.getString(ClOrdID.FIELD), messageHolder);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		try {
			allMessages.getReadWriteLock().writeLock().lock();
			allMessages.add(messageHolder);
		} finally {
			allMessages.getReadWriteLock().writeLock().unlock();
		}
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
		try {
			latestExecutionReportsList.getReadWriteLock().readLock().lock();
			for (MessageHolder holder : latestExecutionReportsList) {
				try {
					if(clOrdID.equals(holder.getMessage().getString(ClOrdID.FIELD))) {
						return holder.getMessage();
					}
				} catch (Exception e) {
					// ignore
				}
			}
		} finally {
			latestExecutionReportsList.getReadWriteLock().readLock().unlock();
		}
		return null;
	}

	// TODO: UUUUUgly
	public Message getOpenOrder(String clOrdID) {
		try {
			allMessages.getReadWriteLock().readLock().lock();
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
		} finally {
			allMessages.getReadWriteLock().readLock().unlock();
		}
	}

	public EventList<MessageHolder> getAllMessagesList() {
		return allMessages;
	}

	public Message getLatestMessage(String clOrdID) {
		try {
			latestMessageList.getReadWriteLock().readLock().lock();
			FilterList<MessageHolder> list = new FilterList<MessageHolder>(latestMessageList,new FIXMatcher<String>(ClOrdID.FIELD, clOrdID));
			if (list.size()>0){
				return list.get(0).getMessage();
			} else {
				return null;
			}
		} finally {
			latestMessageList.getReadWriteLock().readLock().unlock();
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
	
	public void visitOpenOrdersExecutionReports(MessageVisitor visitor)
	{
		try {
			openOrderList.getReadWriteLock().readLock().lock();
			MessageHolder[] holders = openOrderList.toArray(new MessageHolder[0]);
			for(MessageHolder holder : holders)
			{
				visitor.visitOpenOrderExecutionReports(holder.getMessage());
			}
		} finally {
			openOrderList.getReadWriteLock().readLock().unlock();
		}
	}
	
	public MessageHolder getOrder(String clOrdID){
		synchronized (orderMap){
			return orderMap.get(clOrdID);
		}
	}
	
}
