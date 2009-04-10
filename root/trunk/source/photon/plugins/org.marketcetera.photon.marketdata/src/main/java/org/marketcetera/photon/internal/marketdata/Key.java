package org.marketcetera.photon.internal.marketdata;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.marketcetera.photon.model.marketdata.MDItem;

/* $License$ */

/**
 * Base class for keys that identify unique market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class Key<T extends MDItem> {

	private final String mSymbol;

	/**
	 * Constructor.
	 * 
	 * @param symbol
	 *            the symbol
	 */
	public Key(String symbol) {
		Validate.notNull(symbol);
		mSymbol = symbol;
	}

	/**
	 * Returns the symbol.
	 * 
	 * @return the symbol
	 */
	public String getSymbol() {
		return mSymbol;
	}

	@Override
	public int hashCode() {
		// getClass() is included since distinct subclasses are not equal
		return new HashCodeBuilder().append(getClass()).append(mSymbol).toHashCode();
	}

	/**
	 * Subclasses may override this, but must start with:
	 * <p>
	 * <code>if (!super.equals(obj)) return false;</code>
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Key<?> other = (Key<?>) obj;
		return new EqualsBuilder().append(mSymbol, other.mSymbol).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("symbol", mSymbol) //$NON-NLS-1$
				.toString();
	}
}