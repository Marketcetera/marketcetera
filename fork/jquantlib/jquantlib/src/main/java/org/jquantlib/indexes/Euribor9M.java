package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>9-months Euribor index </p>    
 */
public class Euribor9M extends Euribor {


    //
    // public methods
    //

    public Euribor9M(final Handle < YieldTermStructure > h) {
        super(new Period(9, TimeUnit.Months), h);
    }

}
