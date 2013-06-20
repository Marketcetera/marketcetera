/**
 *
 */
package org.jquantlib.indexes;

import org.jquantlib.QL;
import org.jquantlib.currencies.Europe.EURCurrency;
import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.calendars.Target;

/**
 * Actual/365 %Euribor index
 * <p>
 * Euribor rate adjusted for the mismatch between the actual/360
 * convention used for Euribor and the actual/365 convention
 * previously used by a few pre-EUR currencies.
 *
 * @author Ueli Hofstetter
 */
// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
public class Euribor365 extends IborIndex {



    public Euribor365(final Period tenor, final Handle<YieldTermStructure> h) {
        super("Euribor365", tenor,
                2, // settlement days
                new EURCurrency(),
                new Target(),
                euriborConvention(tenor),
                euriborEOM(tenor),
                new Actual365Fixed(),
                h);
        QL.require(this.tenor().units() != TimeUnit.Days , "for daily tenors dedicated DailyTenor constructor must be used"); // QA:[RG]::verified // TODO: message
    }

}