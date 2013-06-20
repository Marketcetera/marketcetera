package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>3-weeks Euribor365 index </p>    
 */
public class Euribor365_3W extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_3W(final Handle < YieldTermStructure > h) {
        super(new Period(3, TimeUnit.Weeks), h);
    }

}
