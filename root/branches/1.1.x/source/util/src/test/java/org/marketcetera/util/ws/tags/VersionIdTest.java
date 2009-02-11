package org.marketcetera.util.ws.tags;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class VersionIdTest
    extends TagTestBase
{
    public static final VersionId TEST_VERSION=
        new VersionId();
    public static final VersionId TEST_VERSION_D=
        new VersionId();


    static {
        TEST_VERSION.setValue("testVersion");
        TEST_VERSION_D.setValue("testVersionD");
    }


    @Test
    public void all()
    {
        VersionId tag=new VersionId();
        tag.setValue(TEST_VALUE);
        VersionId copy=new VersionId();
        copy.setValue(TEST_VALUE);
        single(tag,copy,new VersionId());

        assertNotNull(VersionId.SELF);
        assertNotNull(VersionId.SELF.getValue());
    }
}
