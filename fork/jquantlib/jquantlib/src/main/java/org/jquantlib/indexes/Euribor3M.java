package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>3-months Euribor index </p>    
 */
public class Euribor3M extends Euribor {


    //
    // public methods
    //

    public Euribor3M(final Handle < YieldTermStructure > h) {
        super(new Period(3, TimeUnit.Months), h);
    }

}
