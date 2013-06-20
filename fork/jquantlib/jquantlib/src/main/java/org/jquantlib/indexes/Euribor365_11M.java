package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>11-months Euribor365 index </p>    
 */
public class Euribor365_11M extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_11M(final Handle < YieldTermStructure > h) {
        super(new Period(11, TimeUnit.Months), h);
    }

}
