package org.marketcetera.photon.internal.marketdata;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for keys that identify unique market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class Key {

	private final Instrument mInstrument;

	/**
	 * Constructor.
	 * 
	 * @param instrument
	 *            the instrument
	 */
	public Key(final Instrument instrument) {
		Validate.notNull(instrument);
		mInstrument = instrument;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return the instrument
	 */
	public Instrument getInstrument() {
		return mInstrument;
	}

	@Override
	public final int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder().append(getClass()).append(mInstrument);
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
	public final boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Key otherKey = (Key) obj;
		EqualsBuilder builder = new EqualsBuilder().append(mInstrument, otherKey.mInstrument);
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
	protected void refineEquals(final EqualsBuilder builder, final Key otherKey) {
		// no-op
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("instrument", mInstrument) //$NON-NLS-1$
				.toString();
	}
}