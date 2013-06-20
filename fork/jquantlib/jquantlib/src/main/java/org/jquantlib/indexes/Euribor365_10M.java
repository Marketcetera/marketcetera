package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>10-months Euribor365 index </p>    
 */
public class Euribor365_10M extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_10M(final Handle < YieldTermStructure > h) {
        super(new Period(10, TimeUnit.Months), h);
    }

}
