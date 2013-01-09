/**
 * 
 */
package org.marketcetera.photon.views.fixmessagedetail;

import org.eclipse.core.runtime.Platform;

class FIXMessageDetailTableRow implements Comparable<Object> {
	private String field;

	private int  tag;

	private String value;

	private String valueName;

	private boolean required;

	public FIXMessageDetailTableRow(String field, int tag, String value,
			String valueName, boolean required) {
		this.field = field;
		this.tag = tag;
		this.value = value;
		this.valueName = valueName;
		this.required = required;
		changeNullsToEmptyString();
	}

	private void changeNullsToEmptyString() {
		final String emptyString = ""; //$NON-NLS-1$
		if (field == null) {
			field = emptyString;
		}
		if (value == null) {
			value = emptyString;
		}
		if (valueName == null) {
			valueName = emptyString;
		}
	}

	public Object getColumnValue(int whichColumn) {
		FIXMessageDetailColumnType columnType = FIXMessageDetailColumnType
				.fromColumnIndex(whichColumn);
		return getColumnValue(columnType);
	}

	public Object getColumnValue(FIXMessageDetailColumnType whichColumn) {
		if (whichColumn == FIXMessageDetailColumnType.Field) {
			return field;
		} else if (whichColumn == FIXMessageDetailColumnType.Tag) {
			return tag;
		} else if (whichColumn == FIXMessageDetailColumnType.Value) {
			return value;
		} else if (whichColumn == FIXMessageDetailColumnType.ValueName) {
			return valueName;
		} else if (whichColumn == FIXMessageDetailColumnType.Required) {
			return Boolean.toString(required);
		}
		return null;
	}

	private String getFormattedStringRowEnding() {
		String osStr = Platform.getOS();
		if (osStr != null && osStr.equals(Platform.OS_WIN32)) {
			return "\r\n"; //$NON-NLS-1$
		}
		return "\n"; //$NON-NLS-1$
	}

	public String toFormattedString() {
		final String columnDelimiter = "\t"; //$NON-NLS-1$
		final String rowEnding = getFormattedStringRowEnding();
		StringBuilder builder = new StringBuilder();
		builder.append(field);
		builder.append(columnDelimiter);
		builder.append(tag);
		builder.append(columnDelimiter);
		builder.append(value);
		builder.append(columnDelimiter);
		builder.append(valueName);
		builder.append(columnDelimiter);
		builder.append(required);
		builder.append(rowEnding);
		return builder.toString();
	}

	// implementing Comparable is necessary simply to appease glazed lists which
	// otherwise
	// currently blows up when a new message is added to the list with a natural
	// sort order
	// (i.e., no comparator)
	public int compareTo(Object o) {
		return 0;
	}
}