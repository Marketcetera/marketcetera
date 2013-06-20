package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>6-months Euribor index </p>    
 */
public class Euribor6M extends Euribor {


    //
    // public methods
    //

    public Euribor6M(final Handle < YieldTermStructure > h) {
        super(new Period(6, TimeUnit.Months), h);
    }

}
