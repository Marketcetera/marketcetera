package org.marketcetera.photon.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastForwardPoints;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LastSpotRate;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;
import quickfix.field.Text;
import quickfix.field.TransactTime;
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

	private ClOrdIDComparator clOrdIDComparator;

	private final FIXMessageFactory messageFactory;

	public FIXMessageHistory(FIXMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
		clOrdIDComparator = new ClOrdIDComparator();

		allMessages = new BasicEventList<MessageHolder>();
		allFilteredMessages = new FilterList<MessageHolder>(allMessages);
		fillMessages = new FilterList<MessageHolder>(allFilteredMessages, new FillMatcher());
		orderIDList = new GroupingList<MessageHolder>(allMessages, clOrdIDComparator);
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
	
	public void addIncomingMessage(quickfix.Message fixMessage) {
		if(LoggerAdapter.isDebugEnabled(this) && fixMessage.getHeader().isSetField(SendingTime.FIELD)) {
			long sendingTime =0;
			try {
				sendingTime = fixMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD).getTime();
			} catch (FieldNotFound e) {
			}
			long systemTime = System.currentTimeMillis();
			double diff = (sendingTime-systemTime)/1000.0;
			if(Math.abs(diff) > 1) {
				LoggerAdapter.debug(Thread.currentThread().getName() + ": sendingTime v systemTime: "+diff, this);
			}
		}
		try {
//			if (FIXMessageUtil.isCancelReject(fixMessage)){
//				if (fixMessage.isSetField(field))
//			}
			allMessages.getReadWriteLock().writeLock().lock();
			updateOrderIDMappings(fixMessage);
			allMessages.add(new IncomingMessageHolder(fixMessage));
			if (FIXMessageUtil.isCancelReject(fixMessage) && fixMessage.isSetField(ClOrdID.FIELD) && fixMessage.isSetField(OrdStatus.FIELD)){
				try {
					Message executionReport = getLatestExecutionReport(fixMessage.getString(ClOrdID.FIELD));
					Message newExecutionReport = messageFactory.createMessage(MsgType.EXECUTION_REPORT);
					FIXMessageUtil.fillFieldsFromExistingMessage(newExecutionReport, executionReport, false);
					newExecutionReport.setField(fixMessage.getField(new OrdStatus()));
					if (fixMessage.isSetField(Text.FIELD)){
						newExecutionReport.setField(fixMessage.getField(new Text()));
					}
					if (newExecutionReport.isSetField(ExecType.FIELD)){
						newExecutionReport.setField(new ExecType(ExecType.REJECTED));
					}
					if (newExecutionReport.isSetField(TransactTime.FIELD)) {
						newExecutionReport.setField(new TransactTime());
					}
					newExecutionReport.getHeader().setField(new SendingTime());
					
					newExecutionReport.getHeader().removeField(MsgSeqNum.FIELD);
					newExecutionReport.removeField(ExecID.FIELD);
					newExecutionReport.removeField(LastShares.FIELD);
					newExecutionReport.removeField(LastPx.FIELD);
					newExecutionReport.removeField(LastSpotRate.FIELD);
					newExecutionReport.removeField(LastForwardPoints.FIELD);
					newExecutionReport.removeField(LastMkt.FIELD);
					
					allMessages.add(new IncomingMessageHolder(newExecutionReport));
				} catch (FieldNotFound e) {
					throw new RuntimeException("Should never happen in FIXMessageHistory.addIncomingMessage", e);
				}
			}
		} finally {
			allMessages.getReadWriteLock().writeLock().unlock();
		}
	}

	private void updateOrderIDMappings(quickfix.Message fixMessage) {
		if (fixMessage.isSetField(ClOrdID.FIELD) && fixMessage.isSetField(OrigClOrdID.FIELD))
		{
			try {
				clOrdIDComparator.addIDMap(fixMessage.getString(ClOrdID.FIELD), fixMessage.getString(OrigClOrdID.FIELD));
			} catch (FieldNotFound e) {
				throw new RuntimeException("Should never happen in FIXMessageHistory.updateOrderIDMappings()");
			}
		}
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		updateOrderIDMappings(fixMessage);
		OutgoingMessageHolder messageHolder = new OutgoingMessageHolder(fixMessage);
		if (FIXMessageUtil.isOrderSingle(fixMessage) || FIXMessageUtil.isOrderList(fixMessage)){
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
				Message aMessage = holder.getMessage();
				try {
					if (0 == clOrdIDComparator.compareIDAndMessage(clOrdID, aMessage)){
						return aMessage;
					}
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
			return null;
		} finally {
			latestExecutionReportsList.getReadWriteLock().readLock().unlock();
		}
	}

//	// TODO: UUUUUgly
//	public Message getOpenOrder(String clOrdID) {
//		try {
//			allMessages.getReadWriteLock().readLock().lock();
//			Message returnMessage = null;
//			for (MessageHolder holder : allMessages) {
//				try {
//					Message aMessage = holder.getMessage();
//					if (aMessage.getHeader().getString(MsgType.FIELD).equals(MsgType.ORDER_SINGLE) &&
//							clOrdID.equals(aMessage.getString(ClOrdID.FIELD)))
//					{
//						returnMessage  = aMessage;
//					}
//				} catch (FieldNotFound e) {
//				}
//			}
//			return returnMessage;
//		} finally {
//			allMessages.getReadWriteLock().readLock().unlock();
//		}
//	}

	public EventList<MessageHolder> getAllMessagesList() {
		return allMessages;
	}

	public Message getLatestMessage(String clOrdID) {
		try {
			latestMessageList.getReadWriteLock().readLock().lock();
			for (MessageHolder holder : latestMessageList) {
				Message aMessage = holder.getMessage();
				try {
					if (0 == clOrdIDComparator.compareIDAndMessage(clOrdID, aMessage)){
						return aMessage;
					}
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
			return null;
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
