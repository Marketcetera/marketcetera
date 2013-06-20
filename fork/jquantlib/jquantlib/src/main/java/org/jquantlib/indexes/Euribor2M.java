package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>2-months Euribor index </p>    
 */
public class Euribor2M extends Euribor {


    //
    // public methods
    //

    public Euribor2M(final Handle < YieldTermStructure > h) {
        super(new Period(2, TimeUnit.Months), h);
    }

}
