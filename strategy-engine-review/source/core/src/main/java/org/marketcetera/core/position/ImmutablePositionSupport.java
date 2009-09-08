package org.marketcetera.core.position;

import java.math.BigDecimal;
import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * An implementation of IncomingPositionSupport that provides positions from an fixed map.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ImmutablePositionSupport implements IncomingPositionSupport {

    private final ImmutableMap<? extends PositionKey, BigDecimal> mPositions;

    /**
     * Constructor.
     * 
     * @param positions
     *            a map of positions, cannot be null, cannot have any null keys or values
     */
    public ImmutablePositionSupport(Map<? extends PositionKey, BigDecimal> positions) {
        mPositions = ImmutableMap.copyOf(positions);
    }

    @Override
    public BigDecimal getIncomingPositionFor(PositionKey key) {
        BigDecimal position = mPositions.get(key);
        return position != null ? position : BigDecimal.ZERO;
    }

    @Override
    public ImmutableMap<? extends PositionKey, BigDecimal> getIncomingPositions() {
        return mPositions;
    }

}
