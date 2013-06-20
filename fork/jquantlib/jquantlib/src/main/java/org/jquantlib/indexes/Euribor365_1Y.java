package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-year Euribor365 index </p>    
 */
public class Euribor365_1Y extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_1Y(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Years), h);
    }

}
