package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>4-months Euribor index </p>    
 */
public class Euribor4M extends Euribor {


    //
    // public methods
    //

    public Euribor4M(final Handle < YieldTermStructure > h) {
        super(new Period(4, TimeUnit.Months), h);
    }

}
