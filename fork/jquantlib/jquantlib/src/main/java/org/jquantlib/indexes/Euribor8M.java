package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>8-months Euribor index </p>    
 */
public class Euribor8M extends Euribor {


    //
    // public methods
    //

    public Euribor8M(final Handle < YieldTermStructure > h) {
        super(new Period(8, TimeUnit.Months), h);
    }

}
