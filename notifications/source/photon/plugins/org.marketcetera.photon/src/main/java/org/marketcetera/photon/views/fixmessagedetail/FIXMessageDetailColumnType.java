package org.marketcetera.photon.views.fixmessagedetail;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public enum FIXMessageDetailColumnType
    implements Messages
{
	// There is a hidden first column at index zero.
	Field(1,
	      FIELD_LABEL.getText(),
	      20),
	Tag(2,
	    TAG_LABEL.getText(),
	    10),
	Value(3,
	      VALUE_LABEL.getText(),
	      42),
    ValueName(4,
              VALUE_NAME_LABEL.getText(),
              20),
    Required(5,
             REQUIRED_LABEL.getText(),
             12);

	private String name;

	private int index;

	private int numChars;

	/**
	 * @param numChars
	 *            quantity of characters in a typical column, used when
	 *            computing column width.
	 */
	private FIXMessageDetailColumnType(int index, String name, int numChars) {
		this.name = name;
		this.index = index;
		this.numChars = numChars;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getNumChars() {
		return numChars;
	}

	public static FIXMessageDetailColumnType fromColumnIndex(int columnIndex) {
		FIXMessageDetailColumnType[] columnTypes = FIXMessageDetailColumnType
				.values();
		for (FIXMessageDetailColumnType columnType : columnTypes) {
			if (columnType.getIndex() == columnIndex) {
				return columnType;
			}
		}
		return null;
	}
}