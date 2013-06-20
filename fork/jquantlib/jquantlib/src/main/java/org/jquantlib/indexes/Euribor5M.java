package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>5-months Euribor index </p>    
 */
public class Euribor5M extends Euribor {


    //
    // public methods
    //

    public Euribor5M(final Handle < YieldTermStructure > h) {
        super(new Period(5, TimeUnit.Months), h);
    }

}
