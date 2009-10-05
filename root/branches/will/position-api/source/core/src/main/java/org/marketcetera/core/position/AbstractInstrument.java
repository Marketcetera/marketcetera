package org.marketcetera.core.position;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for instruments. This class establishes an ordering across all
 * subclasses.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractInstrument implements Instrument {

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Instrument))
            return false;
        Instrument otherInstrument = (Instrument) obj;
        return compareTo(otherInstrument) == 0;
    }

    @Override
    public final int compareTo(Instrument o) {
        CompareToBuilder builder = new CompareToBuilder().append(getTypeId(), o
                .getTypeId());
        if (builder.toComparison() == 0) {
            // same type id, allow subclass to order further
            refineCompareTo(builder, o);
        }
        return builder.toComparison();
    }

    /**
     * Refines the builder used to establish instrument ordering.
     * <p>
     * This class guarantees that when this method is called, the
     * <code>this</code> object has the same type id as the
     * <code>otherInstrument</code> object, i.e.
     * <p>
     * <code>getTypeId() == otherInstrument.getTypeId()</code>
     * <p>
     * If both instruments have obeyed the requirements of {@link #getTypeId()},
     * by design it should be safe to assume that both are of the same Java
     * class, i.e. that of the subclass.
     * 
     * @param builder
     *            builder to enhance
     * @param otherInstrument
     *            the other instrument to compare with
     */
    protected abstract void refineCompareTo(CompareToBuilder builder,
            Instrument otherInstrument);

    @Override
    public final int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getTypeId());
        enhanceHashCode(builder);
        return builder.toHashCode();
    }

    /**
     * Refines the builder used to generate a hash code. Two instruments are
     * equal if and only if compareTo returns zero. Therefore, subclasses must
     * ensure that any information used in
     * {@link #refineCompareTo(CompareToBuilder, Instrument)} is used here for
     * the hash code to ensure that equal objects have the same hash code.
     * 
     * @param builder
     *            builder to enhance
     */
    protected abstract void enhanceHashCode(HashCodeBuilder builder);
}
