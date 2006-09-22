package org.marketcetera.datamodel;

import junit.framework.Test;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.datamodel.helpers.HibernateUtil;
import org.marketcetera.datamodel.helpers.PrecannedDataLoader;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Verifies that a series of transactions results in the right state in the db.
 * This is based on the "ibm_exmaple_transactions" SQL file, and we try this:
 * <ol>
 * <li>Create accounts </li>
 * <li>Create an equity</li>
 * <li>Create a buy transaction </li>
 * <li>Create a dividend</li>
 * <li>Create a sell transaction </li>
 * </ol>
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ComprehensiveTransactionTest extends TestCase {
    public ComprehensiveTransactionTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new DBTestSuite(ComprehensiveTransactionTest.class);
    }

    public void testIBM_comprehensive() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        // create the account + sub-accounts
        Account acct = new Account("GS001", "Bollinger", "Goldman Equities 001");
        session.save(acct);
        SubAccount sti = new SubAccount(acct, PrecannedDataLoader.STI);
        SubAccount cash = new SubAccount(acct, PrecannedDataLoader.CASH);
        SubAccount divRev = new SubAccount(acct, PrecannedDataLoader.DIV_REV);
        SubAccount unrealized = new SubAccount(acct, PrecannedDataLoader.UNREALIZED_GAIN_LOSS);
        SubAccount changeOnCOI = new SubAccount(acct, PrecannedDataLoader.CHANGE_ON_CLOSE_OF_INV);
        SubAccount commissions = new SubAccount(acct, PrecannedDataLoader.COMMISIONS);
        session.save(sti);
        session.save(cash);
        session.save(divRev);
        session.save(unrealized);
        session.save(changeOnCOI);
        session.save(commissions);

        // create an equity
        MSymbol ibm = new MSymbol("IBM", "IBM Equity", "IBM");
        session.save(ibm);
        Equity equity = new Equity(ibm);
        session.save(equity);
        session.getTransaction().commit();

        // create a buy transaction
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Calendar purchDate = new GregorianCalendar(2006, 7, 8); // purchase date
        Journal buyJournal = new Journal("Purchase of 100 shares of IBM for 81.25", purchDate.getTime());
        session.save(buyJournal);
        double buyPrice = 100*81.25;
        session.save(new Posting(sti, buyJournal, PrecannedDataLoader.USD, new BigDecimal(buyPrice)));
        session.save(new Posting(cash, buyJournal, PrecannedDataLoader.USD, new BigDecimal(-buyPrice)));
        session.save(new Posting(cash, buyJournal, PrecannedDataLoader.USD, new BigDecimal(-8)));
        session.save(new Posting(commissions, buyJournal, PrecannedDataLoader.USD, new BigDecimal(8)));
        session.save(new Trade(buyJournal, Trade.AssetType.Equity, equity.getId(),
                new BigDecimal(100), acct, Trade.TradeType.BasicTrade));
        session.getTransaction().commit();

        // create a dividend transaction
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Journal dividend = new Journal("IBM pays .25 cent divident on 100 shares", new GregorianCalendar(2006, 7, 10).getTime());
        session.save(dividend);
        session.save(new Posting(cash, dividend, PrecannedDataLoader.USD, new BigDecimal(0.25*100)));
        session.save(new Posting(divRev, dividend, PrecannedDataLoader.USD, new BigDecimal(0.25*100)));
        session.getTransaction().commit();

        // create a sell transaction
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Journal sell = new Journal("Sale of 100 shares of Ibm at 82.25", new GregorianCalendar(2006, 7, 12).getTime());
        session.save(sell);
        double sellPrice = 82.25*100;
        session.save(new Posting(cash, sell, PrecannedDataLoader.USD, new BigDecimal(sellPrice)));
        session.save(new Posting(sti, sell, PrecannedDataLoader.USD, new BigDecimal(-sellPrice)));
        session.save(new Posting(changeOnCOI, sell, PrecannedDataLoader.USD, new BigDecimal(sellPrice-buyPrice)));
        session.save(new Posting(cash, sell, PrecannedDataLoader.USD, new BigDecimal(-8)));
        session.save(new Posting(commissions, sell, PrecannedDataLoader.USD, new BigDecimal(8)));
        session.save(new Trade(sell, Trade.AssetType.Equity, equity.getId(),
                new BigDecimal(-100), acct, Trade.TradeType.BasicTrade));
        session.getTransaction().commit();
    }
}
