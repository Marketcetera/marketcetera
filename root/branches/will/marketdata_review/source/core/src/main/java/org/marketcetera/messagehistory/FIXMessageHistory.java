package org.marketcetera.messagehistory;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIXMessageHistory is an object that stores all incoming and outgoing messages (from Photon, for example)
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageHistory {

	private EventList<MessageHolder> allMessages;
	
	private FilterList<MessageHolder> allFilteredMessages;

	private FilterList<MessageHolder> fillMessages;

    private AveragePriceList averagePriceList;

	private FilterList<MessageHolder> latestExecutionReportsList;

	private FilterList<MessageHolder> latestMessageList;
	
	private FilterList<MessageHolder> openOrderList;
	
	private final Map<String, MessageHolder> orderMap;
	
	private Map<String, String> orderIDToGroupMap;

	private final FIXMessageFactory messageFactory;

	public FIXMessageHistory(FIXMessageFactory messageFactory) {
		this.messageFactory = messageFactory;

		allMessages = new BasicEventList<MessageHolder>();
		allFilteredMessages = new FilterList<MessageHolder>(allMessages);
		fillMessages = new FilterList<MessageHolder>(allFilteredMessages, new FillMatcher());
        GroupingList<MessageHolder> orderIDList = new GroupingList<MessageHolder>(allMessages, new GroupIDComparator());
		latestExecutionReportsList = new FilterList<MessageHolder>(
			new FunctionList<List<MessageHolder>, MessageHolder>(orderIDList,
				new LatestExecutionReportsFunction()), new NotNullMatcher());
		latestMessageList = new FilterList<MessageHolder>(
				new FunctionList<List<MessageHolder>, MessageHolder>(orderIDList,
					new LatestMessageFunction()), new NotNullMatcher());
		averagePriceList = new AveragePriceList(messageFactory, allMessages);
		openOrderList = new FilterList<MessageHolder>(latestExecutionReportsList, new OpenOrderMatcher());
		
		orderMap = new HashMap<String, MessageHolder>();
		orderIDToGroupMap = new HashMap<String, String>();
	}
	
	public void addIncomingMessage(quickfix.Message fixMessage) {
		if(SLF4JLoggerProxy.isDebugEnabled(this) && fixMessage.getHeader().isSetField(SendingTime.FIELD)) {
			long sendingTime =0;
			try {
				sendingTime = fixMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD).getTime(); //non-i18n
			} catch (FieldNotFound ignored) {
                // ignored
            }
			long systemTime = System.currentTimeMillis();
			double diff = (sendingTime-systemTime)/1000.0;
			if(Math.abs(diff) > 1) {
                            SLF4JLoggerProxy.debug(this, "{}: sendingTime v systemTime: {}", Thread.currentThread().getName(), diff); //$NON-NLS-1$
			}
		}
		try {
//			if (FIXMessageUtil.isCancelReject(fixMessage)){
//				if (fixMessage.isSetField(field))
//			}
			allMessages.getReadWriteLock().writeLock().lock();
			updateOrderIDMappings(fixMessage);
			String groupID = getGroupID(fixMessage);
			allMessages.add(new IncomingMessageHolder(fixMessage, groupID));
			if (FIXMessageUtil.isCancelReject(fixMessage) && fixMessage.isSetField(ClOrdID.FIELD) && fixMessage.isSetField(OrdStatus.FIELD)){
				// Add a new execution report to the stream to update the order status, using the values from the 
				// previous execution report.
				try {
					Message executionReport = getLatestExecutionReport(fixMessage.getString(ClOrdID.FIELD));
					Message newExecutionReport = messageFactory.createMessage(MsgType.EXECUTION_REPORT);
					FIXMessageUtil.fillFieldsFromExistingMessage(newExecutionReport, executionReport, false);
					newExecutionReport.setField(fixMessage.getField(new OrdStatus()));
					if (fixMessage.isSetField(Text.FIELD)){
						newExecutionReport.setField(fixMessage.getField(new Text()));
					}
					if (newExecutionReport.isSetField(ExecTransType.FIELD)){
						newExecutionReport.setField(new ExecTransType(ExecTransType.STATUS));
					}
					if (newExecutionReport.isSetField(ExecType.FIELD)){
						newExecutionReport.setField(new ExecType(ExecType.ORDER_STATUS));
					}
					if (newExecutionReport.isSetField(TransactTime.FIELD)) {
						newExecutionReport.setField(new TransactTime(new Date())); //i18n_datetime
					}
					newExecutionReport.getHeader().setField(new SendingTime(new Date())); //i18n_datetime
					
					newExecutionReport.getHeader().removeField(MsgSeqNum.FIELD);
					newExecutionReport.removeField(ExecID.FIELD);
					newExecutionReport.removeField(LastShares.FIELD);
					newExecutionReport.removeField(LastPx.FIELD);
					newExecutionReport.removeField(LastSpotRate.FIELD);
					newExecutionReport.removeField(LastForwardPoints.FIELD);
					newExecutionReport.removeField(LastMkt.FIELD);
					
					allMessages.add(new IncomingMessageHolder(newExecutionReport, groupID));
				} catch (FieldNotFound e) {
					throw new RuntimeException(Messages.SHOULD_NEVER_HAPPEN_IN_ADDINCOMINGMESSAGE.getText(), e);
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
				String origClOrdID = fixMessage.getString(OrigClOrdID.FIELD);
				String clOrdID = fixMessage.getString(ClOrdID.FIELD);
				String groupID;
				// first check to see if the orig is in the map, and if so, use
				// whatever it maps to as the groupID
				if (orderIDToGroupMap.containsKey(origClOrdID)){
					groupID = getGroupID(origClOrdID);
				} else {
					// otherwise, do a mapping from clOrdId -> origClOrdID
					groupID = origClOrdID;
				}
				orderIDToGroupMap.put(clOrdID, groupID);
			} catch (FieldNotFound e) {
                            throw new RuntimeException(Messages.SHOULD_NEVER_HAPPEN_IN_UPDATEORDERIDMAPPINGS.getText());
			}
		}
	}

	public void addOutgoingMessage(quickfix.Message fixMessage) {
		updateOrderIDMappings(fixMessage);
		String groupID = null;
		groupID = getGroupID(fixMessage);

		OutgoingMessageHolder messageHolder = new OutgoingMessageHolder(fixMessage, groupID);
		if (FIXMessageUtil.isOrderSingle(fixMessage) || FIXMessageUtil.isOrderList(fixMessage)
                || FIXMessageUtil.isCancelReplaceRequest(fixMessage)){
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

	private String getGroupID(quickfix.Message fixMessage) {
		String groupID = null;
		try {
			groupID  = getGroupID(fixMessage.getString(ClOrdID.FIELD));
		}catch (FieldNotFound fnf) {
			/* do nothing */
		}
		return groupID;
	}

	private String getGroupID(String clOrdID) {
		if (orderIDToGroupMap.containsKey(clOrdID)){
			return orderIDToGroupMap.get(clOrdID);
		} else {
			return clOrdID;
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
			String groupID = getGroupID(clOrdID);
			if (groupID != null){
				for (MessageHolder holder : latestExecutionReportsList) {
					if (0 == groupID.compareTo(holder.getGroupID())){
						return holder.getMessage();
					}
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
			String groupID = getGroupID(clOrdID);
			if (groupID != null)
			{
				for (MessageHolder holder : latestMessageList)
				{
					String holderGroupID = holder.getGroupID();
					if (holderGroupID != null && 0 == groupID.compareTo(holderGroupID)){
						return holder.getMessage();
					}
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
			MessageHolder[] holders = openOrderList.toArray(new MessageHolder[openOrderList.size()]);
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
