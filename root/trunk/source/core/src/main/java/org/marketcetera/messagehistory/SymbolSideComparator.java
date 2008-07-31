package org.marketcetera.messagehistory;

import java.io.Serializable;
import java.util.Comparator;

import org.marketcetera.core.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.Side;
import quickfix.field.Symbol;

@ClassVersion("$Id$") //$NON-NLS-1$
public class SymbolSideComparator implements Comparator<MessageHolder>,
		Serializable {

	/**
	 * Recommended field for Serializable objects.
	 */
	private static final long serialVersionUID = 6918033431342030636L;

	public int compare(MessageHolder arg0, MessageHolder arg1) {
		Message message0 = arg0.getMessage();
		Message message1 = arg1.getMessage();

		int compareResult = 0;
		if ((compareResult = compareFields(message0, message1, Symbol.FIELD)) != 0) {
			return compareResult;
		}
		if ((compareResult = compareFields(message0, message1, Side.FIELD)) != 0) {
			return compareResult;
		}
		if ((compareResult = compareFields(message0, message1, Account.FIELD)) != 0) {
			return compareResult;
		}
		// all equal
		return 0;
	}

	private int compareFields(Message m1, Message m2, int fieldID) {
		if (!m1.isSetField(fieldID) && !m2.isSetField(fieldID)) {
			return 0;
		}
		if (!m1.isSetField(fieldID)) {
			return -1;
		}
		if (!m2.isSetField(fieldID)) {
			return 1;
		}
		try {
			return m1.getString(fieldID).compareTo(m2.getString(fieldID));
		} catch (FieldNotFound e) {
			// should never happen
			return 1;
		}
	}
}
