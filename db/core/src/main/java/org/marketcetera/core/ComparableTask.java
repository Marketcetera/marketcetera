package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an orderable, executable task.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ComparableTask
        extends Runnable, Comparable<ComparableTask>
{
}
