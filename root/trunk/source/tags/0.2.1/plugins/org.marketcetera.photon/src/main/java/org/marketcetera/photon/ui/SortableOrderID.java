package org.marketcetera.photon.ui;


/**
 * A wrapper for a string-typed Order ID that implements <code>Comparable</code> and first
 * attempts to compare Order IDs as numerics, falling back onto alphanumeric comparison
 * if either ID is not numerical.
 *
 * @author andrei@lissovski.org
 */
public class SortableOrderID implements Comparable<SortableOrderID> {
	
	private String orderId;
	
	
	public SortableOrderID(String orderId) {
		this.orderId = orderId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SortableOrderID that) {
		try {
			//agl attempt to compare as numerics
			Long thisNumericOrderId = Long.valueOf(this.orderId);
			Long thatNumericOrderId = Long.valueOf(that.orderId);
			return thisNumericOrderId.compareTo(thatNumericOrderId);
		} catch (NumberFormatException nfe) {
			//agl either id is not numeric -- compare as strings
			return this.orderId.compareTo(that.orderId);
		}
	}
	
	@Override
	public String toString() {
		return orderId;
	}

}
