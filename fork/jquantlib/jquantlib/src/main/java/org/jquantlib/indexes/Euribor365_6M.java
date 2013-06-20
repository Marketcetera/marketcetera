package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>6-months Euribor365 index </p>    
 */
public class Euribor365_6M extends Euribor365 {


    //
    // public methods
    //

    public Euribor365_6M(final Handle < YieldTermStructure > h) {
        super(new Period(6, TimeUnit.Months), h);
    }

}
