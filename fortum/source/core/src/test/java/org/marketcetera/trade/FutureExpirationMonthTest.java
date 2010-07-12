package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link FutureExpirationMonth}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureExpirationMonthTest
{
    /**
     * Tests {@link FutureExpirationMonth#getByCfiCode(char)} and
     * {@link FutureExpirationMonth#getByCfiCode(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getFutureExpirationMonth()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode(' ');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode(" ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode('E');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getByCfiCode("E");
            }
        };
        int index = 0;
        for(FutureExpirationMonth expirationMonth : FutureExpirationMonth.values()) {
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getByCfiCode(expirationMonth.getCode()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getByCfiCode(Character.toLowerCase(expirationMonth.getCode())));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getByCfiCode(new StringBuffer().append(expirationMonth.getCode()).toString()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getByCfiCode(new StringBuffer().append(expirationMonth.getCode()).toString().toLowerCase()));
            index += 1;
        }
    }
    /**
     * Tests {@link FutureExpirationMonth#getByMonthShortName(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetByDescription()
            throws Exception
    {
       String[] descriptions = new String[] { "jan","Feb","MAR","aPr","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC" };
       String[] badDescriptions = new String[] { null,"","blah","123","10","U"};
       int index = 1;
       for(String code : descriptions) {
           assertEquals(FutureExpirationMonth.values()[index-1],
                        FutureExpirationMonth.getByMonthShortName(code));
           index += 1;
       }
       for(final String badDescription : badDescriptions) {
           new ExpectedFailure<IllegalArgumentException>() {
               @Override
               protected void run()
                       throws Exception
               {
                   FutureExpirationMonth.getByMonthShortName(badDescription);
               }
           };
       }
    }
    /**
     * Tests {@link FutureExpirationMonth#getByMonthOfYear(String)}; 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetByMonthOfYear()
            throws Exception
    {
        for(int counter=-1;counter<=13;counter++) {
            if(counter >= 1 &&
               counter <= 12) {
                FutureExpirationMonth month = FutureExpirationMonth.getByMonthOfYear(String.valueOf(counter));
                assertEquals(FutureExpirationMonth.values()[counter-1],
                             month);
                StringBuffer expectedYear = new StringBuffer();
                if(counter < 10) {
                    expectedYear.append("0");
                }
                expectedYear.append(counter);
                assertEquals(expectedYear.toString(),
                             month.getMonthOfYear());
            } else {
                final int value = counter;
                new ExpectedFailure<IllegalArgumentException>() {
                    @Override
                    protected void run()
                            throws Exception
                    {
                        FutureExpirationMonth.getByMonthOfYear(String.valueOf(value));
                    }
                };
            }
        }
    }
    /**
     * Tests {@link FutureExpirationMonth#getFutureExpirationMonthByWeek(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test@Ignore
    public void testGetByWeekOfYear()
            throws Exception
    {
       for(int counter=-1;counter<=54;counter++) {
           if(counter >=1 &&
              counter <= 53) {
               if(counter == 1) {
                   assertEquals(FutureExpirationMonth.DECEMBER,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2009));
               } else if(counter <= 6) {
                   assertEquals(FutureExpirationMonth.JANUARY,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 10) {
                   assertEquals(FutureExpirationMonth.FEBRUARY,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 14) {
                   assertEquals(FutureExpirationMonth.MARCH,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 18) {
                   assertEquals(FutureExpirationMonth.APRIL,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 23) {
                   assertEquals(FutureExpirationMonth.MAY,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 27) {
                   assertEquals(FutureExpirationMonth.JUNE,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 31) {
                   assertEquals(FutureExpirationMonth.JULY,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 36) {
                   assertEquals(FutureExpirationMonth.AUGUST,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 40) {
                   assertEquals(FutureExpirationMonth.SEPTEMBER,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 45) {
                   assertEquals(FutureExpirationMonth.OCTOBER,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 49) {
                   assertEquals(FutureExpirationMonth.NOVEMBER,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               } else if(counter <= 53) {
                   assertEquals(FutureExpirationMonth.DECEMBER,
                                FutureExpirationMonth.getByWeekOfYear(counter,
                                                                      2010));
               }
           } else {
               final int value = counter;
               new ExpectedFailure<IllegalArgumentException>() {
                   @Override
                   protected void run()
                           throws Exception
                   {
                       FutureExpirationMonth.getByWeekOfYear(value,
                                                             2010);
                   }
               };
           }
       }
    }
}
