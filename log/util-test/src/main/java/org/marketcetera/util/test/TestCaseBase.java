package org.marketcetera.util.test;

import java.io.File;

/**
 * Base class for test cases.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TestCaseBase
{

    // CLASS DATA.

    /**
     * The root directory for test files.
     */

    protected static final String DIR_ROOT=
        "src"+File.separator+"test"+ //$NON-NLS-1$ //$NON-NLS-2$
        File.separator+"sample_data"; //$NON-NLS-1$

    /**
     * The root directory for target files.
     */

    protected static final String DIR_TARGET=
        "target"; //$NON-NLS-1$

    /**
     * The root directory for class files.
     */

    protected static final String DIR_CLASSES=
        DIR_TARGET+File.separator+"classes"; //$NON-NLS-1$

    /**
     * The root directory for test class files.
     */

    protected static final String DIR_TEST_CLASSES=
        DIR_TARGET+File.separator+"test-classes"; //$NON-NLS-1$
}
