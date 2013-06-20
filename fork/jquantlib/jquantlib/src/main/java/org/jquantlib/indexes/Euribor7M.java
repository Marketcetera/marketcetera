package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>7-months Euribor index </p>    
 */
public class Euribor7M extends Euribor {


    //
    // public methods
    //

    public Euribor7M(final Handle < YieldTermStructure > h) {
        super(new Period(7, TimeUnit.Months), h);
    }

}
