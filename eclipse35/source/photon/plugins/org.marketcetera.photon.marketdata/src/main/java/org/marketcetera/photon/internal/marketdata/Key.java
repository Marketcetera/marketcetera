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
 * @since 1.5.0
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
	public final int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder().append(getClass()).append(mSymbol);
		enhanceHashCode(builder);
		return builder.toHashCode();
	}

	/**
	 * Subclasses can override to enhance the builder used to generate the hash code. If they do,
	 * they must also override {@link #refineEquals(EqualsBuilder, Key)} to ensure that the hash
	 * code is consistent with equals.
	 * 
	 * @param builder
	 *            builder to enhance
	 */
	protected void enhanceHashCode(final HashCodeBuilder builder) {
		// no-op
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		// once the classes are equal, we can safely cast to Key<T>
		Key<?> otherKey = (Key<?>) obj;
		EqualsBuilder builder = new EqualsBuilder().append(mSymbol, otherKey.mSymbol);
		refineEquals(builder, otherKey);
		return builder.isEquals();
	}

	/**
	 * Subclasses can override to refine the builder used to establish equality.
	 * 
	 * This class guarantees that when this method is called, the <code>this</code> object has the
	 * same class as the <code>otherKey</code> object, i.e.
	 * <p>
	 * <code>getClass() == otherKey.getClass()</code>
	 * 
	 * @param builder
	 *            builder to enhance
	 * @param otherKey
	 *            the other key to compare with
	 */
	protected void refineEquals(EqualsBuilder builder, Key<?> otherKey) {
		// no-op
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("symbol", mSymbol) //$NON-NLS-1$
				.toString();
	}
}