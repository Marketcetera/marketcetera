package org.marketcetera.core.trade;

import java.io.Serializable;
import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * A financial instrument.
 * <p>
 * Each instrument has a symbol attribute. Sub-types may add more
 * attributes as needed to uniquely identify the instrument being traded.
 * <p>
 * Each instrument sub-type should override the {@link #equals(Object)} &
 * {@link #hashCode()} methods to ensure that any two instrument instances that
 * refer to the same instrument are considered equal.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: Instrument.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: Instrument.java 16063 2012-01-31 18:21:55Z colin $")
@Immutable
public abstract class Instrument implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the symbol value.
	 * 
	 * @return the symbol value.
	 */
	public abstract String getSymbol();
	/**
	 * Returns the security type for this Instrument.
	 * 
	 * @return the security type.
	 */
	public abstract SecurityType getSecurityType();
}