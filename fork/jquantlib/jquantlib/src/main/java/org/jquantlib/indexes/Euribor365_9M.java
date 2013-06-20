package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>9-months Euribor365 index </p>    
 */
public class Euribor365_9M extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_9M(final Handle < YieldTermStructure > h) {
        super(new Period(9, TimeUnit.Months), h);
    }

}
