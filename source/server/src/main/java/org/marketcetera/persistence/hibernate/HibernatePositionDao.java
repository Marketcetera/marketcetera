package org.marketcetera.persistence.hibernate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.systemmodel.persistence.PositionDao;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
public class HibernatePositionDao
        implements PositionDao
{
    /**
     * Create a new HibernatePositionDao instance.
     *
     * @param inSessionFactory
     */
    @Autowired
    public HibernatePositionDao(SessionFactory inSessionFactory)
    {
        Validate.notNull(inSessionFactory,
                         "Session factory missing");
        sessionFactory = inSessionFactory;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.PositionDao#getPositionAsOf(org.marketcetera.trade.Instrument, java.util.Date)
     */
    @Override
    public <T extends Instrument> BigDecimal getPositionAsOf(T inInstrument,
                                                             Date inDate)
    {
        if(inInstrument instanceof Equity) {
        } else if(inInstrument instanceof Future) {
        } else if(inInstrument instanceof Option) {
        } else {
            throw new UnsupportedOperationException();
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.PositionDao#getAllPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /**
    *
    *
    *
    * @return
    */
   private Session currentSession()
   {
       return sessionFactory.getCurrentSession();
   }
   /**
    * 
    */
   private SessionFactory sessionFactory;
}
