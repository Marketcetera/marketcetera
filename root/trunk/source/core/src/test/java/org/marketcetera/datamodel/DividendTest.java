package org.marketcetera.datamodel;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.marketcetera.core.ExpectedTestFailure;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DividendTest extends DBTestBase {
    public DividendTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        suite = new DBTestSuite(DividendTest.class);
        return suite;
    }

    public void testNoEquityReference() throws Exception {
        final Dividend div = new Dividend();

        Equity equity = new Equity();
        equity.setDescription("best stock");
        MSymbol ifli = new MSymbol();
        ifli.setBloomberg("IFLI.IM");
        equity.setMSymbol(ifli);

        div.setAmount(new BigDecimal("42.37"));
        Calendar cal = new GregorianCalendar();
        cal.set(2006, 7, 8);
        div.setAnnounceDate(cal.getTime());

        div.setCurrency(Currency.USD);

        cal.set(2007, 1,1);
        div.setPayableDate(cal.getTime());

        cal.set(2007, 2, 2);
        div.setExDate(cal.getTime());

        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(equity);
        session.save(ifli);

        new ExpectedTestFailure(PropertyValueException.class, "Dividend.equity") {
            protected void execute() throws Throwable {
                session.save(div);
            }
        }.run();

        // reset the session since it can't be used after exception
        session.disconnect();
        Session session2 = HibernateUtil.getSessionFactory().openSession();
        session2.beginTransaction();
        div.setEquity(equity);
        session2.save(div);
        session2.getTransaction().commit();
    }

    public void testNoCurrencyReference() throws Exception {
        final Dividend div = new Dividend();

        Equity equity = new Equity();
        equity.setDescription("best stock");
        div.setEquity(equity);

        // set msymbol
        MSymbol ifli = new MSymbol();
        ifli.setBloomberg("IFLI.IM");
        equity.setMSymbol(ifli);

        div.setAmount(new BigDecimal("42.37"));
        Calendar cal = new GregorianCalendar();
        cal.set(2006, 7, 8);
        div.setAnnounceDate(cal.getTime());

        cal.set(2007, 1,1);
        div.setPayableDate(cal.getTime());

        cal.set(2007, 2, 2);
        div.setExDate(cal.getTime());

        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(equity);
        session.save(ifli);

        new ExpectedTestFailure(PropertyValueException.class, "Dividend.currency") {
            protected void execute() throws Throwable {
                session.save(div);
            }
        }.run();

        div.setCurrency(Currency.USD);

        // reset the session since it can't be used after exception
        session.disconnect();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        session2.beginTransaction();
        session2.save(div);
        session2.getTransaction().commit();
    }
}
