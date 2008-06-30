/**
 * 
 */
package org.marketcetera.photon.views.fixmessagedetail;

public enum FIXMessageDetailColumnType {
	// There is a hidden first column at index zero.
	Field(1, "Field", 20), Tag(2, "Tag", 10), Value(3, "Value", 42), ValueName(
			4, "Value Name", 20), Required(5, "Required", 12);

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