package com.marketcetera.fix.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import quickfix.SessionID;

/* $License$ */

/**
 * Tests {@link HibernateMessageStore}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/test.xml"})
public class HibernateMessageStoreTest
        implements ApplicationContextAware
{
    /**
     * Execute a basic performance test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test(timeout=30000)
    public void testPerformance()
            throws Exception
    {
        MessageStoreMessageDao messageDao = applicationContext.getBean(MessageStoreMessageDao.class);
        SessionID sessionId = new SessionID("FIX.4.2:SENDER->RECEIVER");
        HibernateMessageStore messageStore = new HibernateMessageStore(sessionId);
        int messageCount = 10000;
        for(int i=1;i<=messageCount;i++) {
            messageStore.set(i,
                             "message-"+i);
        }
        while(true) {
            if(messageDao.count() >= messageCount) {
                break;
            }
            Thread.sleep(250);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * test application context
     */
    private ApplicationContext applicationContext;
}
