package org.marketcetera.persist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public abstract class AbstractMultiEntityQuery<Clazz extends EntityBase>
{
    public AbstractMultiEntityQuery<Clazz> all()
    {
        // by default, get all fields
        from = query.from(dataType);
        return this;
    }
    public AbstractMultiEntityQuery()
    {
        dataType = getEntityType();
        initialize();
    }
    public List<Clazz> fetch()
    {
        if(!initialized) {
            // TODO look for a transport mechanism and send off to the mothership if available, otherwise throw
            throw new IllegalStateException("No datastore available in this node and no transport mechanism available");
        }
        Predicate predicate;
        if(where == null) {
            if(like == null) {
                throw new PersistenceException("Must provide either a where or like predicate");
            }
            // use like predicate
            predicate = like;
        } else {
            predicate = where;
        }
        SLF4JLoggerProxy.debug(this,
                               "Using predicate: {}",
                               predicate);
        CriteriaQuery<Clazz> activeQuery = select.where(predicate);
        // test end
        return new ArrayList<Clazz>();
    }
    protected abstract Class<Clazz> getEntityType();
    protected final void initialize()
    {
        initialized = false;
        ApplicationContextRepository repository = ApplicationContextRepository.getInstance();
        if(repository == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Delaying initialization of {} because no application context repository is available");
            return;
        }
        // next, get the application context
        ApplicationContext context = repository.getApplicationContext();
        if(context == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Delaying initialization of {} because no application context is available");
            return;
        }
        // look for an entity manager bean: depending on which node we're in, there may or may not be one available
        try {
            entityManager = context.getBean(EntityManager.class);
            criteriaBuilder = entityManager.getCriteriaBuilder();
            query = criteriaBuilder.createQuery(dataType);
            from = query.from(dataType);
            select = query.select(from);
            initialized = true;
        } catch (NoSuchBeanDefinitionException e) {
            SLF4JLoggerProxy.debug(this,
                                   "Delaying initialization of {} because no entity manager bean is available");
            return;
        }
    }
    private final Class<Clazz> dataType;
    private boolean initialized = false;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Clazz> query;
    private Root<Clazz> from;
    private CriteriaQuery<Clazz> select;
    private Predicate where = null;
    private Predicate like = null;
}
