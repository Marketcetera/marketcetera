/**
 * 
 */
package org.marketcetera.photon.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrderID;

/**
 * Compares messages by the value of {@link ClOrdID}
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class ClOrdIDComparator implements Comparator<MessageHolder>, Serializable {
	/**
	 * Recommended field for Serializable objects.
	 */
	private static final long serialVersionUID = -833621597934763848L;
	
	private Map<String, String> clOrdIDOrigClOrdIDMap = new HashMap<String, String>();
	

	/**
	 * Compares the specified messages by the value of the {@link ClOrdID}
	 * field, if any.  It checks for the lack of a {@link ClOrdID}
	 * and responds appropriately, or calls {@link String#compareTo(String)}
	 * if both messages have a {@link ClOrdID}.
	 * @param msg0 the first message to compare
	 * @param msg1 the second message to compare
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(MessageHolder msg0, MessageHolder msg1) {
		Message message0 = msg0.getMessage();
		Message message1 = msg1.getMessage();
		
		if (message0.isSetField(ClOrdID.FIELD)){
			if (message1.isSetField(ClOrdID.FIELD)){
				try {
					String clOrdID0 = message0.getString(ClOrdID.FIELD);
					return compareIDAndMessage(clOrdID0, message1);
				} catch (Exception e) {
					throw new RuntimeException("Should never happen in ClOrdIDComparator.compare()");
				}
			} else {
				return 1;
			}
		} else {
			if (message0.isSetField(OrderID.FIELD) && message1.isSetField(OrderID.FIELD)){
				try {
					return message0.getString(OrderID.FIELD).compareTo(message1.getString(OrderID.FIELD));
				} catch (Exception e) {
					throw new RuntimeException("Should never happen in ClOrdIDComparator.compare()");
				}
			} else {
				return message1.isSetField(ClOrdID.FIELD) ? -1 : 0;
			}
		}
		
//		try {
//			Message message0;
//			Message message1;
//			if (msg0.getMessageReference() > msg1.getMessageReference()){
//				message0 = msg1.getMessage();
//				message1 = msg0.getMessage();
//			} else {
//				message0 = msg0.getMessage();
//				message1 = msg1.getMessage();
//			}
//			
//			if (message0.getHeader().isSetField(MsgSeqNum.FIELD) && message1.getHeader().isSetField(MsgSeqNum.FIELD)){
//				assert message0.getHeader().getInt(MsgSeqNum.FIELD) <= message1.getHeader().getInt(MsgSeqNum.FIELD);
//			}
//			if (message0.isSetField(OrigClOrdID.FIELD) && message1.isSetField(OrigClOrdID.FIELD)){
//				// both have OrigClOrdID
//				return compareMessages(message0, OrigClOrdID.FIELD, message1, OrigClOrdID.FIELD);
//			} else if (message0.isSetField(OrigClOrdID.FIELD) && !message1.isSetField(OrigClOrdID.FIELD)){
//				// only message0 has OrigClOrdID
//				if (message1.isSetField(ClOrdID.FIELD)){
//					return compareMessages(message0, OrigClOrdID.FIELD, message1, ClOrdID.FIELD);
//				} else {
//					return -1;
//				}
//			} else if (message1.isSetField(OrigClOrdID.FIELD)){
//				// only message1 has OrigClOrdID
//				if (message0.isSetField(ClOrdID.FIELD))
//				{
//					return compareMessages(message0, ClOrdID.FIELD, message1, OrigClOrdID.FIELD);
//				} else {
//					return 1;
//				}
//			} else {
//				return compareMessages(message0, ClOrdID.FIELD, message1, ClOrdID.FIELD);
//			}
//			
//		} catch (FieldNotFound ex){
//			// this should never happen
//			return 1;
//		}
	}

	public int compareIDAndMessage(String clOrdID0, Message message1)
			throws FieldNotFound {
		String clOrdID1 = message1.getString(ClOrdID.FIELD);
		if (clOrdID0.equals(clOrdID1) || chainExists(clOrdID0, clOrdID1) || chainExists(clOrdID1, clOrdID0)){
			return 0;
		} else {
			return clOrdID0.compareTo(clOrdID1);
		}
	}

//	private int compareMessages(Message message0, int field0, Message message1,
//			int field1) throws FieldNotFound {
//		if (message0.isSetField(field0) && message1.isSetField(field1)){
//			// both fields set, do string compare
//			return message0.getString(field0).compareTo(message1.getString(field1));
//		} else if (message0.isSetField(field0)){
//			// only message0 has field
//			return 1;
//		} else if (!message0.isSetField(field0) && !message1.isSetField(field1)) {
//			// neither field present
//			return 0;
//		} else {
//			// only message1 has field
//			return -1;
//		}
//	}
	
	private boolean chainExists(String clOrdID0, String clOrdID1) {
		String mapped;
		String toMap = clOrdID0;
		while ((mapped = clOrdIDOrigClOrdIDMap.get(toMap))!=null){
			if (clOrdID1.equals(mapped)){
				return true;
			}
			toMap = mapped;
		}
		return false;
	}

	public void addIDMap(String clOrdID, String origClOrdID){
		clOrdIDOrigClOrdIDMap.put(clOrdID, origClOrdID);
	}
}