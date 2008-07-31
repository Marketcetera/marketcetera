package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import static org.marketcetera.persist.Messages.*;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.util.List;

/* $License$ */
/**
 * Base classes for testing entities that extend
 * {@link org.marketcetera.persist.NDEntityBase}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class CorePersistNDTestBase<E extends NDEntityBase,
        S extends SummaryNDEntityBase> extends NDEntityTestBase<E,S> {
    @BeforeClass
    public static void springSetup() throws Exception {
            springSetup(new String[]{"persist.xml"}); //$NON-NLS-1$
    }

}
