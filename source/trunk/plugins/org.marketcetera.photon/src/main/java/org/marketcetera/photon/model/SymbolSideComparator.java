package org.marketcetera.photon.model;

import java.io.Serializable;
import java.util.Comparator;

import org.marketcetera.core.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.Side;
import quickfix.field.Symbol;

@ClassVersion("$Id$")
public class SymbolSideComparator implements Comparator<MessageHolder>, Serializable {
	
	/**
	 * Recommended field for Serializable objects.
	 */
	private static final long serialVersionUID = 6918033431342030636L;

	public int compare(MessageHolder arg0, MessageHolder arg1) {
		try {
			Message message0 = arg0.getMessage();
			Message message1 = arg1.getMessage();
	
			if (!message0.isSetField(Symbol.FIELD) || !message1.isSetField(Symbol.FIELD) ||
					!message0.isSetField(Side.FIELD) || !message1.isSetField(Side.FIELD)
					)
			{
				return 1;
			}
			
			int accountComparator;
			String account0 = null;
			String account1 = null;
			try { account0 = message0.getString(Account.FIELD); } catch (FieldNotFound ex) { /* do nothing */ }
			try { account1 = message1.getString(Account.FIELD); } catch (FieldNotFound ex) { /* do nothing */ }
			
			if (account0 == null || account1 == null)
			{
				accountComparator = (account0 == null && account1 == null) ? 0 : (account0 == null ? 1 : -1);
			} else {
				accountComparator = (account0.compareTo(account1));
			}

			
			String symbolString0 = message0.getString(Symbol.FIELD);
			String side0 = message0.getString(Side.FIELD);
			String symbolString1 = message1.getString(Symbol.FIELD);
			String side1 = message1.getString(Side.FIELD);

			if (accountComparator == 0) {
				int symbolComparator = symbolString0.compareTo(symbolString1);
				if (symbolComparator == 0){
					int sideComparator = side0.compareTo(side1);
					return sideComparator;
				} else {
					return symbolComparator;
				}
			} else {
				return accountComparator;
			}
		} catch (FieldNotFound ex){
			// this should never happen
			return 1;
		}
	}
}