package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-year Euribor index </p>    
 */
public class Euribor1Y extends Euribor {


    //
    // public methods
    //

    public Euribor1Y(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Years), h);
    }

}
