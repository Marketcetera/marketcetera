package org.marketcetera.messagehistory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
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
	
	private final Map<String, MessageHolder> originalOrderACKs;
	
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
		
		originalOrderACKs = new HashMap<String, MessageHolder>();
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
			IncomingMessageHolder messageHolder = new IncomingMessageHolder(fixMessage, groupID);
			
			// The first message that comes in with a specific order id gets stored in a map.  This
			// map is used by #getFirstReport(String) to facilitate CancelReplace
			// TODO: Change this to look for custom ORS acks
            if (fixMessage.isSetField(ClOrdID.FIELD) && fixMessage.isSetField(OrdStatus.FIELD) && FIXMessageUtil.isExecutionReport(fixMessage)) {
				try {
					String id = fixMessage.getString(ClOrdID.FIELD);
					char status = fixMessage.getChar(OrdStatus.FIELD);
					if (status == OrdStatus.PENDING_NEW || status == OrdStatus.PENDING_REPLACE) {
						synchronized (originalOrderACKs) {
							if (!originalOrderACKs.containsKey(id)) {
								originalOrderACKs.put(id, messageHolder);
							}
						}
					}
				} catch (FieldNotFound e) {
					// This should not happen, the get field calls are guarded with fixMessage.isSetField
					ExceptUtils.swallow(e);
				}
			}			
			
			allMessages.add(messageHolder);
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
	
	/**
	 * Returns a {@link MessageHolder} holding the first report Photon received
	 * for the given clOrdID. This is the PENDING NEW or PENDING REPLACE message
	 * added via {@link #addIncomingMessage(Message)}.
	 * 
	 * @param clOrdID
	 *            the clOrdID
	 * @return the MessageHolder holding the first report
	 */
	public MessageHolder getFirstReport(String clOrdID){
		synchronized (originalOrderACKs){
			return originalOrderACKs.get(clOrdID);
		}
	}
	
}
