package org.marketcetera.trade;

import static org.junit.Assert.*;
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
     * Tests {@link FutureExpirationMonth#getFutureExpirationMonth(char)} and
     * {@link FutureExpirationMonth#getFutureExpirationMonth(String)}.
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
                FutureExpirationMonth.getFutureExpirationMonth(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth(' ');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth(" ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth('E');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth("E");
            }
        };
        int index = 0;
        for(FutureExpirationMonth expirationMonth : FutureExpirationMonth.values()) {
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(expirationMonth.getCode()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(Character.toLowerCase(expirationMonth.getCode())));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(new StringBuffer().append(expirationMonth.getCode()).toString()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(new StringBuffer().append(expirationMonth.getCode()).toString().toLowerCase()));
            index += 1;
        }
    }
    /**
     * Tests {@link FutureExpirationMonth#getFutureExpirationMonthByDescription(String)}.
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
                        FutureExpirationMonth.getFutureExpirationMonthByDescription(code));
           index += 1;
       }
       for(final String badDescription : badDescriptions) {
           new ExpectedFailure<IllegalArgumentException>() {
               @Override
               protected void run()
                       throws Exception
               {
                   FutureExpirationMonth.getFutureExpirationMonthByDescription(badDescription);
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
    @Test
    public void testGetByWeekOfYear()
            throws Exception
    {
       for(int counter=-1;counter<=53;counter++) {
           if(counter >=1 &&
              counter <= 52) {
               if(counter <= 5) {
                   assertEquals(FutureExpirationMonth.JANUARY,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 9) {
                   assertEquals(FutureExpirationMonth.FEBRUARY,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 13) {
                   assertEquals(FutureExpirationMonth.MARCH,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 17) {
                   assertEquals(FutureExpirationMonth.APRIL,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 22) {
                   assertEquals(FutureExpirationMonth.MAY,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 26) {
                   assertEquals(FutureExpirationMonth.JUNE,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 31) {
                   assertEquals(FutureExpirationMonth.JULY,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 35) {
                   assertEquals(FutureExpirationMonth.AUGUST,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 39) {
                   assertEquals(FutureExpirationMonth.SEPTEMBER,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 44) {
                   assertEquals(FutureExpirationMonth.OCTOBER,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 48) {
                   assertEquals(FutureExpirationMonth.NOVEMBER,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               } else if(counter <= 52) {
                   assertEquals(FutureExpirationMonth.DECEMBER,
                                FutureExpirationMonth.getFutureExpirationMonthByWeek(counter,
                                                                                     2010));
               }
           } else {
               final int value = counter;
               new ExpectedFailure<IllegalArgumentException>() {
                   @Override
                   protected void run()
                           throws Exception
                   {
                       FutureExpirationMonth.getFutureExpirationMonthByWeek(value,
                                                                            2010);
                   }
               };
           }
       }
    }
}
