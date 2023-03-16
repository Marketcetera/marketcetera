package org.marketcetera.strategy;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.core.ClientStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectStrategyClient
        implements StrategyClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#start()
     */
    @Override
    public void start()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Starting direct strategy client");
        Validate.notNull(applicationContext);
        userService = applicationContext.getBean(UserService.class);
        strategyService = applicationContext.getBean(StrategyService.class);
        SLF4JLoggerProxy.debug(this,
                               "Direct strategy client {} owned by user {}",
                               clientId,
                               username);
        user = userService.findByName(username);
        Validate.notNull(user);
        isRunning = true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#stop()
     */
    @Override
    public void stop()
            throws Exception
    {
        try {
            SLF4JLoggerProxy.info(this,
                                  "Stopping direct strategy client");
        } finally {
            isRunning = false;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return isRunning;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#addClientStatusListener(org.marketcetera.core.ClientStatusListener)
     */
    @Override
    public void addClientStatusListener(ClientStatusListener inListener)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#removeClientStatusListener(org.marketcetera.core.ClientStatusListener)
     */
    @Override
    public void removeClientStatusListener(ClientStatusListener inListener)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#getStrategyInstances()
     */
    @Override
    public Collection<? extends StrategyInstance> getStrategyInstances()
    {
        return strategyService.getStrategyInstances(username);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#loadStrategyInstance(org.marketcetera.strategy.StrategyInstance)
     */
    @Override
    public StrategyStatus loadStrategyInstance(StrategyInstance inStrategyInstance)
    {
        return strategyService.loadStrategyInstance(inStrategyInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#getStrategyMessages(java.lang.String, org.marketcetera.core.notifications.INotification.Severity, org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<? extends StrategyMessage> getStrategyMessages(String inStrategyName,
                                                                                 Severity inSeverity,
                                                                                 PageRequest inPageRequest)
    {
        return strategyService.getStrategyMessages(inStrategyName,
                                                   inSeverity,
                                                   inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#findByName(java.lang.String)
     */
    @Override
    public Optional<? extends StrategyInstance> findByName(String inName)
    {
        return strategyService.findByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#uploadFile(org.marketcetera.strategy.FileUploadRequest)
     */
    @Override
    public void uploadFile(FileUploadRequest inRequest)
            throws IOException, NoSuchAlgorithmException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#emitMessage(org.marketcetera.core.notifications.INotification.Severity, java.lang.String)
     */
    @Override
    public void emitMessage(Severity inSeverity,
                            String inMessage)
    {
        StrategyMessage strategyMessage = getStrategyMessageFactory().create();
        strategyMessage.setSeverity(inSeverity);
        strategyMessage.setMessage(inMessage);
        strategyMessage.setStrategyInstance(getStrategyInstanceHolder().getStrategyInstance());
        getStrategyService().createStrategyMessage(strategyMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#startStrategyInstance(java.lang.String)
     */
    @Override
    public void startStrategyInstance(String inStrategyInstanceName)
    {
        strategyService.startStrategyInstance(inStrategyInstanceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#stopStrategyInstance(java.lang.String)
     */
    @Override
    public void stopStrategyInstance(String inStrategyInstanceName)
    {
        strategyService.stopStrategyInstance(inStrategyInstanceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#unloadStrategyInstance(java.lang.String)
     */
    @Override
    public void unloadStrategyInstance(String inStrategyInstanceName)
    {
        strategyService.unloadStrategyInstance(inStrategyInstanceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#addStrategyEventListener(org.marketcetera.strategy.StrategyEventListener)
     */
    @Override
    public void addStrategyEventListener(StrategyEventListener inListener)
    {
        strategyService.addStrategyEventListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClient#removeStrategyEventListener(org.marketcetera.strategy.StrategyEventListener)
     */
    @Override
    public void removeStrategyEventListener(StrategyEventListener inListener)
    {
        strategyService.removeStrategyEventListener(inListener);
    }
    /**
     * Create a new DirectStrategyClient instance.
     *
     * @param inApplicationContext an <code>ApplicationContext</code> value
     * @param inUsername a <code>String</code> value
     */
    public DirectStrategyClient(ApplicationContext inApplicationContext,
                                String inUsername)
    {
        applicationContext = inApplicationContext;
        username = StringUtils.trimToNull(inUsername);
        Validate.notNull(username);
    }
    /**
     * 
     *
     *
     * @return
     */
    private StrategyInstanceHolder getStrategyInstanceHolder()
    {
        if(strategyInstanceHolder == null) {
            strategyInstanceHolder = applicationContext.getBean(StrategyInstanceHolder.class);
        }
        return strategyInstanceHolder;
    }
    /**
     * 
     *
     *
     * @return
     */
    private StrategyMessageFactory getStrategyMessageFactory()
    {
        if(strategyMessageFactory == null) {
            strategyMessageFactory = applicationContext.getBean(StrategyMessageFactory.class);
        }
        return strategyMessageFactory;
    }
    /**
     * 
     *
     *
     * @return
     */
    private StrategyService getStrategyService()
    {
        if(strategyService == null) {
            strategyService = applicationContext.getBean(StrategyService.class);
        }
        return strategyService;
    }
    /**
     * provides access to the application context
     */
    private final ApplicationContext applicationContext;
    /**
     * name of user
     */
    private final String username;
    /**
     * user which owns the activity of this client
     */
    private User user;
    /**
     * indicates if the client is running or not
     */
    private boolean isRunning = false;
    /**
     * provides access to user services
     */
    private UserService userService;
    /**
     * provides access to strategy services
     */
    private StrategyService strategyService;
    /**
     * uniquely identifies this client
     */
    private final String clientId = PlatformServices.generateId();
    /**
     * identifies the strategy instance that manages the running strategy
     */
    private StrategyInstanceHolder strategyInstanceHolder;
    /**
     * creates stratege message objects
     */
    private StrategyMessageFactory strategyMessageFactory;
}
