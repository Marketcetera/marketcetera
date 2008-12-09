package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.List;
import java.util.Arrays;

/* $License$ */
/**
 * Base class for testing FIX char value based enums.
 *
 * @param <E> The Enum type.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class FIXCharEnumTestBase<E extends Enum<E>>
        extends FIXEnumTestBase<Character,E>{
    @Override
    protected List<Character> unknownFIXValues() {
        return Arrays.asList(' ', '?', Character.MIN_VALUE,
                Character.MAX_VALUE);
    }
}
