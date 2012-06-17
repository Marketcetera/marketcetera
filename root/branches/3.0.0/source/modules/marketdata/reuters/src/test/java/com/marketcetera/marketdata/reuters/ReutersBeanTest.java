package com.marketcetera.marketdata.reuters;

import org.junit.Test;
import org.marketcetera.dao.hibernate.HibernateTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersBeanTest.java 82376 2012-06-07 17:10:13Z colin $
 * @since $Release$
 */
public class ReutersBeanTest
        extends HibernateTestBase
{
    @Test
    public void testBean()
            throws Exception
    {
        ReutersBean bean = getApp().getContext().getBeanFactory().createBean(ReutersBean.class);
        SLF4JLoggerProxy.debug(ReutersBeanTest.class,
                               "Created {}",
                               bean);
    }
}
