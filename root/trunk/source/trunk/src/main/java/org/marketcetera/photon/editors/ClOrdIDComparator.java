/**
 * 
 */
package org.marketcetera.photon.editors;

import java.util.Comparator;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.MessageHolder;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

/**
 * Compares messages by the value of {@link ClOrdID}
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class ClOrdIDComparator implements Comparator<MessageHolder> {
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
	public int compare(MessageHolder arg0, MessageHolder arg1) {
		try {
			Message message0 = arg0.getMessage();
			Message message1 = arg1.getMessage();
	
			if (!message0.isSetField(ClOrdID.FIELD) || !message1.isSetField(ClOrdID.FIELD)){
				return message0.isSetField(ClOrdID.FIELD) ? 1 : 0;
			}
			String ordID0 = message0.getString(ClOrdID.FIELD);
			String ordID1 = message1.getString(ClOrdID.FIELD);
			return ordID0.compareTo(ordID1);
		} catch (FieldNotFound ex){
			// this should never happen
			return 1;
		}
	}
}