package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>10-months Euribor index </p>    
 */
public class Euribor10M extends Euribor {


    //
    // public methods
    //

    public Euribor10M(final Handle < YieldTermStructure > h) {
        super(new Period(10, TimeUnit.Months), h);
    }

}
