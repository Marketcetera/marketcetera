package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>11-months Euribor index </p>    
 */
public class Euribor11M extends Euribor {


    //
    // public methods
    //

    public Euribor11M(final Handle < YieldTermStructure > h) {
        super(new Period(11, TimeUnit.Months), h);
    }

}
