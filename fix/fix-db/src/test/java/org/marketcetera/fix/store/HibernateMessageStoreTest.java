package org.marketcetera.fix.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.fix.FixDbTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import quickfix.SessionID;

/* $License$ */

/**
 * Tests {@link HibernateMessageStore}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=FixDbTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera","com.marketcetera"})
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera","com.marketcetera"})
public class HibernateMessageStoreTest
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
    /**
     * provides access to the message store
     */
    @Autowired
    private MessageStoreMessageDao messageDao;
}
