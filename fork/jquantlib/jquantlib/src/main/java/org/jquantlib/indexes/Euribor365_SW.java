package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-week Euribor365 index </p>    
 */
public class Euribor365_SW extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_SW(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Weeks), h);
    }

}
