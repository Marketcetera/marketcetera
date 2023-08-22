package org.marketcetera.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.strategy.dao.PersistentStrategyInstance;
import org.marketcetera.strategy.dao.PersistentStrategyMessage;
import org.marketcetera.strategy.dao.StrategyInstanceDao;
import org.marketcetera.strategy.dao.StrategyMessageDao;
import org.marketcetera.test.DareTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link StrategyService} features.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class StrategyServerTest
        extends DareTestBase
{
    /**
     * Test the ability to unload instances with extant messages.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUnloadInstanceWithMessages()
            throws Exception
    {
        assertTrue(strategyInstanceDao.findAll().isEmpty());
        assertTrue(strategyMessageDao.findAll().isEmpty());
        PersistentStrategyInstance instance1 = generateStrategyInstance();
        PersistentStrategyInstance instance2 = generateStrategyInstance();
        assertEquals(2,
                     strategyInstanceDao.findAll().size());
        assertTrue(strategyMessageDao.findAll().isEmpty());
        generateStrategyMessages(instance1);
        generateStrategyMessages(instance2);
        assertEquals(2,
                     strategyInstanceDao.findAll().size());
        assertEquals(Severity.values().length*2,
                     strategyMessageDao.findAll().size());
        strategyService.unloadStrategyInstance(instance1.getName());
        assertEquals(1,
                     strategyInstanceDao.findAll().size());
        assertEquals(Severity.values().length,
                     strategyMessageDao.findAll().size());
    }
    /**
     * Generate strategy messages for the given strategy instance.
     *
     * @param inStrategyInstance a <code>PersistentStrategyInstance</code> value
     */
    private void generateStrategyMessages(PersistentStrategyInstance inStrategyInstance)
    {
        List<PersistentStrategyMessage> messagesToPersist = Lists.newArrayList();
        for(Severity severity : Severity.values()) {
            PersistentStrategyMessage strategyMessage = new PersistentStrategyMessage();
            strategyMessage.setMessage(UUID.randomUUID().toString());
            strategyMessage.setMessageTimestamp(new Date());
            strategyMessage.setSeverity(severity);
            strategyMessage.setStrategyInstance(inStrategyInstance);
            messagesToPersist.add(strategyMessage);
        }
        strategyMessageDao.saveAll(messagesToPersist);
    }
    /**
     * Generate a persisted strategy instance.
     *
     * @return a <code>PersistentStrategyInstance</code> value
     */
    private PersistentStrategyInstance generateStrategyInstance()
    {
        PersistentStrategyInstance strategyInstance = new PersistentStrategyInstance();
        strategyInstance.setFilename("/tmp/somefile");
        strategyInstance.setHash("hash-of-some-file");
        strategyInstance.setName(UUID.randomUUID().toString());
        strategyInstance.setNonce(strategyInstance.getNonce());
        strategyInstance.setStatus(StrategyStatus.STOPPED);
        strategyInstance.setUser(traderUser);
        strategyInstance = strategyInstanceDao.save(strategyInstance);
        return strategyInstance;
    }
    /**
     * provides access to strategy services
     */
    @Autowired
    private StrategyService strategyService;
    /**
     * provides access to the {@link StrategyMessage} datastore
     */
    @Autowired
    private StrategyMessageDao strategyMessageDao;
    /**
     * provides access to the {@link StrategyInstance} datastore
     */
    @Autowired
    private StrategyInstanceDao strategyInstanceDao;
}
